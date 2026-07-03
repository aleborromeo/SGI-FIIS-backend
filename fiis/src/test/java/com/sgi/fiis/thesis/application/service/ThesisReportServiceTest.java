package com.sgi.fiis.thesis.application.service;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.*;
import com.sgi.fiis.thesis.domain.exception.BusinessRuleViolationException;
import com.sgi.fiis.thesis.domain.port.out.DocumentValidationPort;
import com.sgi.fiis.thesis.domain.port.out.ThesisPlanRepositoryPort;
import com.sgi.fiis.thesis.domain.port.out.ThesisReportRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ThesisReportService Unit Tests")
class ThesisReportServiceTest {

    @Mock
    private ThesisReportRepositoryPort informeRepository;
    @Mock
    private ThesisPlanRepositoryPort planRepository;
    @Mock
    private DocumentValidationPort documentoValidation;

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private ThesisReportService service;

    @BeforeEach
    void setUp() {
        service = new ThesisReportService(informeRepository, planRepository, documentoValidation);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(Long id, String role) {
        CustomUserDetails userDetails = new CustomUserDetails(
                id, "user@unas.edu.pe", "password", true,
                List.of(new SimpleGrantedAuthority(role))
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("registrarInformeFinal - successfully registers report")
    void registrarInformeFinalSuccessfully() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");

        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );

        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(documentoValidation.existeDocumentoActivo(200)).thenReturn(true);

        when(informeRepository.save(any(ThesisReport.class))).thenAnswer(inv -> {
            ThesisReport report = inv.getArgument(0);
            return new ThesisReport(
                    5, report.getIdPlanTesis(), report.getTituloFinal(),
                    report.getIdDocumentoTesis(), null, report.getEstadoInforme()
            );
        });

        RegisterThesisReportCommand cmd = new RegisterThesisReportCommand(12, "AI Final Title", 200);

        ThesisReportResponse response = service.registrarInformeFinal(cmd);

        assertNotNull(response);
        assertEquals(5, response.idInformeTesis());
        assertEquals("AI Final Title", response.tituloFinal());
        assertEquals(ThesisReportStatus.EN_REVISION, response.estadoInforme());

        verify(informeRepository).save(any(ThesisReport.class));
    }

    @Test
    @DisplayName("registrarInformeFinal - throws exception when plan is not approved")
    void registrarInformeFinalThrowsWhenPlanNotApproved() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");

        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );

        when(planRepository.findById(12)).thenReturn(Optional.of(plan));

        RegisterThesisReportCommand cmd = new RegisterThesisReportCommand(12, "AI Final Title", 200);

        assertThrows(BusinessRuleViolationException.class, () -> service.registrarInformeFinal(cmd));
        verifyNoInteractions(informeRepository);
    }

    @Test
    @DisplayName("aprobarInforme - successfully approves report")
    void aprobarInformeSuccessfully() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");

        ThesisReport existingReport = new ThesisReport(
                5, 12, "AI Final Title", 200, null, ThesisReportStatus.EN_REVISION
        );
        when(informeRepository.findById(5)).thenReturn(Optional.of(existingReport));
        when(informeRepository.save(any(ThesisReport.class))).thenAnswer(inv -> inv.getArgument(0));

        ThesisReportResponse response = service.aprobarInforme(5);

        assertNotNull(response);
        assertEquals(ThesisReportStatus.APROBADO, existingReport.getEstadoInforme());
        verify(informeRepository).save(existingReport);
    }

    @Test
    @DisplayName("observarInforme - successfully observes report")
    void observarInformeSuccessfully() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");

        ThesisReport existingReport = new ThesisReport(
                5, 12, "AI Final Title", 200, null, ThesisReportStatus.EN_REVISION
        );
        when(informeRepository.findById(5)).thenReturn(Optional.of(existingReport));
        when(informeRepository.save(any(ThesisReport.class))).thenAnswer(inv -> inv.getArgument(0));

        ThesisReportResponse response = service.observarInforme(5, "Correcciones menores");

        assertNotNull(response);
        assertEquals(ThesisReportStatus.OBSERVADO, existingReport.getEstadoInforme());
        verify(informeRepository).save(existingReport);
    }

    @Test
    @DisplayName("obtenerPorId - retrieves report by ID successfully")
    void obtenerPorIdSuccessfully() {
        ThesisReport existingReport = new ThesisReport(
                5, 12, "AI Final Title", 200, null, ThesisReportStatus.EN_REVISION
        );
        when(informeRepository.findById(5)).thenReturn(Optional.of(existingReport));

        ThesisReportResponse response = service.obtenerPorId(5);

        assertNotNull(response);
        assertEquals(5, response.idInformeTesis());
    }

    @Test
    @DisplayName("listarPorPlan - lists reports by plan ID successfully")
    void listarPorPlanSuccessfully() {
        ThesisReport existingReport = new ThesisReport(
                5, 12, "AI Final Title", 200, null, ThesisReportStatus.EN_REVISION
        );
        when(informeRepository.findByPlanTesis(12)).thenReturn(List.of(existingReport));

        List<ThesisReportResponse> list = service.listarPorPlan(12);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(5, list.get(0).idInformeTesis());
    }

    @Test
    @DisplayName("validarRolDirector - throws exception when user is not director")
    void validateDirectorRoleThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        assertThrows(BusinessRuleViolationException.class, () -> service.aprobarInforme(5));
    }

    @Test
    @DisplayName("registrarInformeFinal - throws exception when document is inactive")
    void documentInactiveThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(12, "AI", "Abstract", 101L, 1, 2, 99, ThesisPlanStatus.APROBADO, null, null);
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(documentoValidation.existeDocumentoActivo(200)).thenReturn(false);

        RegisterThesisReportCommand cmd = new RegisterThesisReportCommand(12, "AI Final", 200);
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarInformeFinal(cmd));
    }

    @Test
    @DisplayName("registrarInformeFinal - throws exception when plan not found")
    void planNotFoundThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        when(planRepository.findById(12)).thenReturn(Optional.empty());

        RegisterThesisReportCommand cmd = new RegisterThesisReportCommand(12, "AI Final", 200);
        assertThrows(com.sgi.fiis.thesis.domain.exception.ThesisPlanNotFoundException.class, () -> service.registrarInformeFinal(cmd));
    }

    @Test
    @DisplayName("obtenerInforme - throws exception when report not found")
    void reportNotFoundThrows() {
        when(informeRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(com.sgi.fiis.thesis.domain.exception.ThesisReportNotFoundException.class, () -> service.obtenerPorId(999));
    }

    @Test
    @DisplayName("status transitions - approve throws when report not found")
    void approveNotFoundThrows() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");
        when(informeRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(com.sgi.fiis.thesis.domain.exception.ThesisReportNotFoundException.class, () -> service.aprobarInforme(999));
    }

    @Test
    @DisplayName("status transitions - observe throws when status is already APROBADO")
    void observeApprovedReportThrows() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");
        ThesisReport report = new ThesisReport(5, 12, "AI", 200, null, ThesisReportStatus.APROBADO);
        when(informeRepository.findById(5)).thenReturn(Optional.of(report));
        assertThrows(com.sgi.fiis.thesis.domain.exception.InvalidStateTransitionException.class, () -> service.observarInforme(5, "Motivo"));
    }
}
