package com.sgi.fiis.thesis.application.service;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.*;
import com.sgi.fiis.thesis.domain.exception.BusinessRuleViolationException;
import com.sgi.fiis.thesis.domain.exception.PlanAccessDeniedException;
import com.sgi.fiis.thesis.domain.port.out.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
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
@DisplayName("ThesisPlanService Unit Tests")
class ThesisPlanServiceTest {

    @Mock
    private ThesisPlanRepositoryPort planRepository;
    @Mock
    private ProcedureWorkflowPort tramiteWorkflow;
    @Mock
    private DocumentValidationPort documentoValidation;
    @Mock
    private ResearchGroupValidationPort grupoValidation;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    private ThesisPlanService service;

    @BeforeEach
    void setUp() {
        service = new ThesisPlanService(planRepository, tramiteWorkflow, documentoValidation, grupoValidation, jdbcTemplate);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthentication(Long id, String role) {
        CustomUserDetails userDetails = new CustomUserDetails(
                id, "student@unas.edu.pe", "password", true,
                List.of(new SimpleGrantedAuthority(role))
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    @DisplayName("registrarPlan - successfully registers plan when validations pass")
    void registrarPlanSuccessfully() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");

        RegisterThesisPlanCommand command = new RegisterThesisPlanCommand(
                "Artificial Intelligence in Education",
                "Thesis abstract here...",
                1, 2, 99
        );

        when(grupoValidation.existeGrupoActivo(2)).thenReturn(true);
        when(grupoValidation.existeLineaActiva(1)).thenReturn(true);
        when(grupoValidation.lineaPerteneceAlGrupo(2, 1)).thenReturn(true);
        when(documentoValidation.existeDocumentoActivo(99)).thenReturn(true);
        when(documentoValidation.documentoPerteneceAUsuario(99, 101L)).thenReturn(true);

        when(planRepository.save(any(ThesisPlan.class))).thenAnswer(invocation -> {
            ThesisPlan p = invocation.getArgument(0);
            return new ThesisPlan(
                    12, p.getTituloTesis(), p.getResumen(), p.getIdEstudiante(),
                    p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), null, null
            );
        });

        when(tramiteWorkflow.crearTramitePlanTesis(12, 101L, 2)).thenReturn(500);
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_COORDINADOR");
        when(tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(12)).thenReturn("COORDINADOR_GRUPO");

        ThesisPlanResponse response = service.registrarPlan(command);

        assertNotNull(response);
        assertEquals(12, response.idPlanTesis());
        assertEquals("Artificial Intelligence in Education", response.tituloTesis());
        assertEquals(500, response.idTramite());
        assertEquals(ThesisProcedureStatus.PENDIENTE_COORDINADOR, response.estadoTramite());
        assertEquals(ReviewerRole.COORDINADOR_GRUPO, response.revisorActual());

        verify(planRepository).save(any(ThesisPlan.class));
        verify(tramiteWorkflow).crearTramitePlanTesis(12, 101L, 2);
    }

    @Test
    @DisplayName("registrarPlan - throws exception when requester is not a student")
    void registrarPlanThrowsWhenNotStudent() {
        mockAuthentication(202L, "ROLE_COORDINADOR_GRUPO");

        RegisterThesisPlanCommand command = new RegisterThesisPlanCommand(
                "AI Thesis", "Abstract", 1, 2, 99
        );

        assertThrows(BusinessRuleViolationException.class, () -> service.registrarPlan(command));
        verifyNoInteractions(planRepository, tramiteWorkflow);
    }

    @Test
    @DisplayName("aprobarPorCoordinador - coordinator approves plan successfully")
    void aprobarPorCoordinadorSuccessfully() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");

        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );

        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);
        when(planRepository.save(any(ThesisPlan.class))).thenAnswer(inv -> inv.getArgument(0));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);

        ThesisPlanResponse response = service.aprobarPorCoordinador(12);

        assertNotNull(response);
        assertEquals(ThesisPlanStatus.APROBADO, existingPlan.getEstadoPlan());
        verify(tramiteWorkflow).derivarPlanTesis(
                eq(12), eq(303L), eq(ThesisProcedureStatus.PENDIENTE_DIRECCION),
                eq(ReviewerRole.DIRECTOR_INVESTIGACION), eq("APROBAR_COORDINADOR"), any(), any()
        );
    }

    @Test
    @DisplayName("observarPorCoordinador - coordinator observes plan successfully")
    void observarPorCoordinadorSuccessfully() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);
        when(planRepository.save(any(ThesisPlan.class))).thenAnswer(inv -> inv.getArgument(0));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);

        ObserveThesisPlanCommand cmd = new ObserveThesisPlanCommand("Falta bibliografía", 100);
        ThesisPlanResponse response = service.observarPorCoordinador(12, cmd);

        assertNotNull(response);
        assertEquals(ThesisPlanStatus.OBSERVADO, existingPlan.getEstadoPlan());
        verify(tramiteWorkflow).derivarPlanTesis(
                12, 303L, ThesisProcedureStatus.OBSERVADO,
                ReviewerRole.ESTUDIANTE, "OBSERVAR_COORDINADOR", "Falta bibliografía", 100
        );
    }

    @Test
    @DisplayName("rechazarPorCoordinador - coordinator rejects plan successfully")
    void rechazarPorCoordinadorSuccessfully() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);
        when(planRepository.save(any(ThesisPlan.class))).thenAnswer(inv -> inv.getArgument(0));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);

        ThesisPlanResponse response = service.rechazarPorCoordinador(12, "Fuera de ámbito");

        assertNotNull(response);
        assertEquals(ThesisPlanStatus.RECHAZADO, existingPlan.getEstadoPlan());
        verify(tramiteWorkflow).derivarPlanTesis(
                eq(12), eq(303L), eq(ThesisProcedureStatus.RECHAZADO),
                eq(ReviewerRole.SIN_REVISOR), eq("RECHAZAR_COORDINADOR"), eq("Fuera de ámbito"), any()
        );
    }

    @Test
    @DisplayName("aprobarPorDirector - director approves plan successfully")
    void aprobarPorDirectorSuccessfully() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));
        when(planRepository.save(any(ThesisPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        ThesisPlanResponse response = service.aprobarPorDirector(12);

        assertNotNull(response);
        verify(tramiteWorkflow).derivarPlanTesis(
                eq(12), eq(404L), eq(ThesisProcedureStatus.PENDIENTE_DECANATO),
                eq(ReviewerRole.DECANO), eq("APROBAR_DIRECTOR"), any(), any()
        );
    }

    @Test
    @DisplayName("observarPorDirector - director observes plan successfully")
    void observarPorDirectorSuccessfully() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));
        when(planRepository.save(any(ThesisPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        ObserveThesisPlanCommand cmd = new ObserveThesisPlanCommand("Faltan firmas", 100);
        ThesisPlanResponse response = service.observarPorDirector(12, cmd);

        assertNotNull(response);
        assertEquals(ThesisPlanStatus.OBSERVADO, existingPlan.getEstadoPlan());
        verify(tramiteWorkflow).derivarPlanTesis(
                12, 404L, ThesisProcedureStatus.OBSERVADO,
                ReviewerRole.COORDINADOR_GRUPO, "OBSERVAR_DIRECTOR", "Faltan firmas", 100
        );
    }

    @Test
    @DisplayName("subsanarPlan - student rectifies observed plan successfully")
    void subsanarPlanSuccessfully() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.OBSERVADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));
        when(documentoValidation.existeDocumentoActivo(101)).thenReturn(true);
        when(planRepository.save(any(ThesisPlan.class))).thenAnswer(inv -> inv.getArgument(0));

        RectifyThesisPlanCommand cmd = new RectifyThesisPlanCommand(101, "Nuevo resumen", "Corregido bibliografía");
        ThesisPlanResponse response = service.subsanarPlan(12, cmd);

        assertNotNull(response);
        assertEquals(ThesisPlanStatus.POSTULADO, existingPlan.getEstadoPlan());
        assertEquals("Nuevo resumen", existingPlan.getResumen());
        assertEquals(101, existingPlan.getIdDocumentoActual());
        verify(tramiteWorkflow).derivarPlanTesis(
                12, 101L, ThesisProcedureStatus.SUBSANADO,
                ReviewerRole.COORDINADOR_GRUPO, "SUBSANAR_PLAN_TESIS", "Corregido bibliografía", 101
        );
    }

    @Test
    @DisplayName("registrarResolucion - dean registers resolution successfully")
    void registrarResolucionSuccessfully() {
        mockAuthentication(505L, "ROLE_DECANO");
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_DECANATO");

        RegisterResolutionCommand cmd = new RegisterResolutionCommand(
                "RES-001", java.time.LocalDate.now(), "Asunto de prueba", 200
        );

        ThesisPlanResponse response = service.registrarResolucion(12, cmd);

        assertNotNull(response);
        verify(tramiteWorkflow).registrarResolucion(
                eq(12), eq(505L), eq("RES-001"), any(), eq("Asunto de prueba"), eq(200)
        );
    }

    @Test
    @DisplayName("obtenerPorId - retrieves thesis plan successfully")
    void obtenerPorIdSuccessfully() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));

        ThesisPlanResponse response = service.obtenerPorId(12);

        assertNotNull(response);
        assertEquals(12, response.idPlanTesis());
    }

    @Test
    @DisplayName("listarPorEstudiante - lists all student thesis plans")
    void listarPorEstudianteSuccessfully() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findByEstudiante(101L)).thenReturn(List.of(existingPlan));

        List<ThesisPlanResponse> list = service.listarPorEstudiante(101L);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("listarPorGrupo - lists all thesis plans for research group")
    void listarPorGrupoSuccessfully() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);

        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findByGrupo(2)).thenReturn(List.of(existingPlan));

        List<ThesisPlanResponse> list = service.listarPorGrupo(2);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("listarPendientesPorRevisor - lists pending plans for a coordinator reviewer")
    void listarPendientesPorRevisorSuccessfully() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        when(tramiteWorkflow.findPlanTesisIdsByRevisor(ReviewerRole.COORDINADOR_GRUPO)).thenReturn(List.of(12));
        ThesisPlan existingPlan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(existingPlan));

        List<ThesisPlanResponse> list = service.listarPendientesPorRevisor(ReviewerRole.COORDINADOR_GRUPO);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("validations - throws error when group is inactive")
    void groupInactiveThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        RegisterThesisPlanCommand command = new RegisterThesisPlanCommand("AI", "Abstract", 1, 2, 99);
        when(grupoValidation.existeGrupoActivo(2)).thenReturn(false);
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarPlan(command));
    }

    @Test
    @DisplayName("validations - throws error when line is inactive")
    void lineInactiveThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        RegisterThesisPlanCommand command = new RegisterThesisPlanCommand("AI", "Abstract", 1, 2, 99);
        when(grupoValidation.existeGrupoActivo(2)).thenReturn(true);
        when(grupoValidation.existeLineaActiva(1)).thenReturn(false);
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarPlan(command));
    }

    @Test
    @DisplayName("validations - throws error when line does not belong to group")
    void lineDoesNotBelongToGroupThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        RegisterThesisPlanCommand command = new RegisterThesisPlanCommand("AI", "Abstract", 1, 2, 99);
        when(grupoValidation.existeGrupoActivo(2)).thenReturn(true);
        when(grupoValidation.existeLineaActiva(1)).thenReturn(true);
        when(grupoValidation.lineaPerteneceAlGrupo(2, 1)).thenReturn(false);
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarPlan(command));
    }

    @Test
    @DisplayName("validations - throws error when document is inactive")
    void documentInactiveThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        RegisterThesisPlanCommand command = new RegisterThesisPlanCommand("AI", "Abstract", 1, 2, 99);
        when(grupoValidation.existeGrupoActivo(2)).thenReturn(true);
        when(grupoValidation.existeLineaActiva(1)).thenReturn(true);
        when(grupoValidation.lineaPerteneceAlGrupo(2, 1)).thenReturn(true);
        when(documentoValidation.existeDocumentoActivo(99)).thenReturn(false);
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarPlan(command));
    }

    @Test
    @DisplayName("validations - throws error when document does not belong to student")
    void documentNotBelongsToStudentThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        RegisterThesisPlanCommand command = new RegisterThesisPlanCommand("AI", "Abstract", 1, 2, 99);
        when(grupoValidation.existeGrupoActivo(2)).thenReturn(true);
        when(grupoValidation.existeLineaActiva(1)).thenReturn(true);
        when(grupoValidation.lineaPerteneceAlGrupo(2, 1)).thenReturn(true);
        when(documentoValidation.existeDocumentoActivo(99)).thenReturn(true);
        when(documentoValidation.documentoPerteneceAUsuario(99, 101L)).thenReturn(false);
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarPlan(command));
    }

    @Test
    @DisplayName("validarRolCoordinador - throws exception when user is not coordinator")
    void validateCoordinatorRoleThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(12, "AI", "Abstract", 101L, 1, 2, 99, ThesisPlanStatus.POSTULADO, null, null);
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        assertThrows(BusinessRuleViolationException.class, () -> service.aprobarPorCoordinador(12));
    }

    @Test
    @DisplayName("validarRolDirector - throws exception when user is not director")
    void validateDirectorRoleThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(12, "AI", "Abstract", 101L, 1, 2, 99, ThesisPlanStatus.APROBADO, null, null);
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        assertThrows(BusinessRuleViolationException.class, () -> service.aprobarPorDirector(12));
    }

    @Test
    @DisplayName("validarRolDecano - throws exception when user is not dean")
    void validateDeanRoleThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(12, "AI", "Abstract", 101L, 1, 2, 99, ThesisPlanStatus.APROBADO, null, null);
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        RegisterResolutionCommand cmd = new RegisterResolutionCommand("RES-01", java.time.LocalDate.now(), "Asunto", 99);
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarResolucion(12, cmd));
    }

    @Test
    @DisplayName("resolverIdEstudianteSegunRol - throws exception when authentication is null")
    void resolverIdEstudianteNullAuthThrows() {
        when(securityContext.getAuthentication()).thenReturn(null);
        assertThrows(BusinessRuleViolationException.class, () -> service.listarPorEstudiante(null));
    }

    @Test
    @DisplayName("validarRevisorParaRol - throws exception when unauthorized reviewer access")
    void validateRevisorRoleThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        assertThrows(BusinessRuleViolationException.class, () -> service.listarPendientesPorRevisor(ReviewerRole.COORDINADOR_GRUPO));
    }

    @Test
    @DisplayName("obtenerPlan - throws exception when plan not found")
    void planNotFoundThrows() {
        when(planRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(com.sgi.fiis.thesis.domain.exception.ThesisPlanNotFoundException.class, () -> service.obtenerPorId(999));
    }

    @Test
    @DisplayName("status transitions - coordinator approve throws when status not POSTULADO")
    void coordinatorApproveInvalidStatusThrows() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan plan = new ThesisPlan(12, "AI", "Abstract", 101L, 1, 2, 99, ThesisPlanStatus.APROBADO, null, null);
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);
        assertThrows(BusinessRuleViolationException.class, () -> service.aprobarPorCoordinador(12));
    }

    @Test
    @DisplayName("status transitions - director approve throws when status not APROBADO")
    void directorApproveInvalidStatusThrows() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");
        ThesisPlan plan = new ThesisPlan(12, "AI", "Abstract", 101L, 1, 2, 99, ThesisPlanStatus.POSTULADO, null, null);
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        assertThrows(BusinessRuleViolationException.class, () -> service.aprobarPorDirector(12));
    }

    @Test
    @DisplayName("status transitions - rectify throws when status not OBSERVADO")
    void rectifyInvalidStatusThrows() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(12, "AI", "Abstract", 101L, 1, 2, 99, ThesisPlanStatus.POSTULADO, null, null);
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        RectifyThesisPlanCommand cmd = new RectifyThesisPlanCommand(99, "Abstract", "Resuelto");
        assertThrows(BusinessRuleViolationException.class, () -> service.subsanarPlan(12, cmd));
    }

    @Test
    @DisplayName("status transitions - register resolution throws when status not PENDIENTE_DECANATO")
    void registerResolutionInvalidStatusThrows() {
        mockAuthentication(505L, "ROLE_DECANO");
        ThesisPlan plan = new ThesisPlan(12, "AI", "Abstract", 101L, 1, 2, 99, ThesisPlanStatus.POSTULADO, null, null);
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        RegisterResolutionCommand cmd = new RegisterResolutionCommand("RES-01", java.time.LocalDate.now(), "Asunto", 99);
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarResolucion(12, cmd));
    }

    @Test
    @DisplayName("listarPorGrupo - student is blocked")
    void listarPorGrupoStudentBlocked() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        assertThrows(BusinessRuleViolationException.class, () -> service.listarPorGrupo(2));
    }

    @Test
    @DisplayName("listarPorGrupo - coordinator of other group is blocked")
    void listarPorGrupoOtherGroupCoordinatorBlocked() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(false);
        assertThrows(BusinessRuleViolationException.class, () -> service.listarPorGrupo(2));
    }

    @Test
    @DisplayName("listarPorGrupo - coordinator of same group is allowed")
    void listarPorGrupoSameGroupCoordinatorAllowed() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);
        when(planRepository.findByGrupo(2)).thenReturn(List.of());
        List<ThesisPlanResponse> list = service.listarPorGrupo(2);
        assertNotNull(list);
    }

    @Test
    @DisplayName("validarRevisorParaRol - check all cases")
    void checkAllRevisorRoleMappings() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        assertNotNull(service.listarPendientesPorRevisor(ReviewerRole.ESTUDIANTE));

        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        assertNotNull(service.listarPendientesPorRevisor(ReviewerRole.COORDINADOR_GRUPO));

        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");
        assertNotNull(service.listarPendientesPorRevisor(ReviewerRole.DIRECTOR_INVESTIGACION));

        mockAuthentication(505L, "ROLE_DECANO");
        assertNotNull(service.listarPendientesPorRevisor(ReviewerRole.DECANO));
    }

    @Test
    @DisplayName("listarPorEstudiante - decano only sees PENDIENTE_DECANATO plans")
    void listarPorEstudianteDecanoFiltersPendienteDecanato() {
        mockAuthentication(505L, "ROLE_DECANO");

        ThesisPlan planDecanato = new ThesisPlan(
                10, "Thesis A", "Abstract A", 200L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        ThesisPlan planCoord = new ThesisPlan(
                11, "Thesis B", "Abstract B", 200L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findByEstudiante(200L)).thenReturn(List.of(planDecanato, planCoord));
        when(tramiteWorkflow.obtenerIdTramitePorPlanTesis(10)).thenReturn(100);
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(10)).thenReturn("PENDIENTE_DECANATO");
        when(tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(10)).thenReturn("DECANO");
        when(tramiteWorkflow.obtenerIdTramitePorPlanTesis(11)).thenReturn(101);
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(11)).thenReturn("PENDIENTE_COORDINADOR");
        when(tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(11)).thenReturn("COORDINADOR_GRUPO");

        List<ThesisPlanResponse> list = service.listarPorEstudiante(200L);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(10, list.get(0).idPlanTesis());
    }

    @Test
    @DisplayName("listarPorEstudiante - decano returns empty when no PENDIENTE_DECANATO plans")
    void listarPorEstudianteDecanoReturnsEmpty() {
        mockAuthentication(505L, "ROLE_DECANO");

        ThesisPlan planCoord = new ThesisPlan(
                11, "Thesis B", "Abstract B", 200L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findByEstudiante(200L)).thenReturn(List.of(planCoord));
        when(tramiteWorkflow.obtenerIdTramitePorPlanTesis(11)).thenReturn(101);
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(11)).thenReturn("PENDIENTE_COORDINADOR");
        when(tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(11)).thenReturn("COORDINADOR_GRUPO");

        List<ThesisPlanResponse> list = service.listarPorEstudiante(200L);

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("listarPorEstudiante - non-decano sees all plans")
    void listarPorEstudianteNonDecanoSeesAll() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");

        ThesisPlan planDecanato = new ThesisPlan(
                10, "Thesis A", "Abstract A", 200L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        ThesisPlan planCoord = new ThesisPlan(
                11, "Thesis B", "Abstract B", 200L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findByEstudiante(200L)).thenReturn(List.of(planDecanato, planCoord));
        when(tramiteWorkflow.obtenerIdTramitePorPlanTesis(10)).thenReturn(100);
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(10)).thenReturn("PENDIENTE_DECANATO");
        when(tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(10)).thenReturn("DECANO");
        when(tramiteWorkflow.obtenerIdTramitePorPlanTesis(11)).thenReturn(101);
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(11)).thenReturn("PENDIENTE_COORDINADOR");
        when(tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(11)).thenReturn("COORDINADOR_GRUPO");

        List<ThesisPlanResponse> list = service.listarPorEstudiante(200L);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("obtenerPorId - decano can access PENDIENTE_DECANATO plan")
    void decanoCanAccessPendienteDecanatoPlan() {
        mockAuthentication(505L, "ROLE_DECANO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_DECANATO");
        when(tramiteWorkflow.obtenerIdTramitePorPlanTesis(12)).thenReturn(100);
        when(tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(12)).thenReturn("DECANO");

        ThesisPlanResponse response = service.obtenerPorId(12);

        assertNotNull(response);
        assertEquals(12, response.idPlanTesis());
    }

    @Test
    @DisplayName("obtenerPorId - decano denied access to non-PENDIENTE_DECANATO plan")
    void decanoDeniedAccessToNonPendienteDecanatoPlan() {
        mockAuthentication(505L, "ROLE_DECANO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_COORDINADOR");

        assertThrows(PlanAccessDeniedException.class, () -> service.obtenerPorId(12));
    }

    private enum CoordinatorOperation {
        APPROVE, OBSERVE, REJECT
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("coordinator not in same group throws for all operations")
    @EnumSource(CoordinatorOperation.class)
    void coordinatorNotInSameGroupThrows(CoordinatorOperation op) {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(false);

        Executable action = switch (op) {
            case APPROVE -> () -> service.aprobarPorCoordinador(12);
            case OBSERVE -> () -> service.observarPorCoordinador(12,
                    new ObserveThesisPlanCommand("Observación", 100));
            case REJECT -> () -> service.rechazarPorCoordinador(12, "Motivo");
        };
        assertThrows(BusinessRuleViolationException.class, action);
        verify(planRepository, never()).save(any());
    }

    @Test
    @DisplayName("rechazarPorCoordinador - throws when motivo is blank")
    void rechazarPorCoordinadorThrowsWhenMotivoIsBlank() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);

        assertThrows(BusinessRuleViolationException.class, () -> service.rechazarPorCoordinador(12, "   "));
        verify(planRepository, never()).save(any());
    }

    @Test
    @DisplayName("rechazarPorCoordinador - throws when motivo is null")
    void rechazarPorCoordinadorThrowsWhenMotivoIsNull() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(true);

        assertThrows(BusinessRuleViolationException.class, () -> service.rechazarPorCoordinador(12, null));
        verify(planRepository, never()).save(any());
    }

    @Test
    @DisplayName("subsanarPlan - throws when student is not the plan owner")
    void subsanarPlanThrowsWhenStudentIsNotOwner() {
        mockAuthentication(999L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.OBSERVADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));

        RectifyThesisPlanCommand cmd = new RectifyThesisPlanCommand(101, "Nuevo resumen", "Corregido");
        assertThrows(BusinessRuleViolationException.class, () -> service.subsanarPlan(12, cmd));
        verify(planRepository, never()).save(any());
    }

    @Test
    @DisplayName("subsanarPlan - throws when provided document does not exist")
    void subsanarPlanThrowsWhenDocumentNotFound() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.OBSERVADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(documentoValidation.existeDocumentoActivo(101)).thenReturn(false);

        RectifyThesisPlanCommand cmd = new RectifyThesisPlanCommand(101, "Nuevo resumen", "Corregido");
        assertThrows(BusinessRuleViolationException.class, () -> service.subsanarPlan(12, cmd));
        verify(planRepository, never()).save(any());
    }

    @Test
    @DisplayName("subsanarPlan - throws when no document and resumen is blank")
    void subsanarPlanThrowsWhenNullDocAndBlankResumen() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.OBSERVADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));

        RectifyThesisPlanCommand cmd = new RectifyThesisPlanCommand(null, "   ", "Corregido");
        assertThrows(BusinessRuleViolationException.class, () -> service.subsanarPlan(12, cmd));
        verify(planRepository, never()).save(any());
    }

    @Test
    @DisplayName("subsanarPlan - throws when no document and resumen is null")
    void subsanarPlanThrowsWhenNullDocAndNullResumen() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.OBSERVADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));

        RectifyThesisPlanCommand cmd = new RectifyThesisPlanCommand(null, null, "Corregido");
        assertThrows(BusinessRuleViolationException.class, () -> service.subsanarPlan(12, cmd));
        verify(planRepository, never()).save(any());
    }

    @Test
    @DisplayName("registrarResolucion - throws when tramite is not PENDIENTE_DECANATO")
    void registrarResolucionThrowsWhenTramiteNotPendienteDecanato() {
        mockAuthentication(505L, "ROLE_DECANO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_COORDINADOR");

        RegisterResolutionCommand cmd = new RegisterResolutionCommand(
                "RES-01", java.time.LocalDate.now(), "Asunto", 99
        );
        assertThrows(BusinessRuleViolationException.class, () -> service.registrarResolucion(12, cmd));
        verifyNoInteractions(jdbcTemplate);
    }

    @Test
    @DisplayName("obtenerPorId - throws when student tries to access another student's plan")
    void obtenerPorIdThrowsWhenStudentViewsOtherStudentPlan() {
        mockAuthentication(999L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));

        assertThrows(PlanAccessDeniedException.class, () -> service.obtenerPorId(12));
    }

    @Test
    @DisplayName("obtenerPorId - throws when director tries to access non-PENDIENTE_DIRECCION plan")
    void obtenerPorIdThrowsWhenDirectorAccessNonPendienteDireccionPlan() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_COORDINADOR");

        assertThrows(PlanAccessDeniedException.class, () -> service.obtenerPorId(12));
    }

    @Test
    @DisplayName("obtenerPorId - coordinator from other group can access plan when PENDIENTE_COORDINADOR")
    void obtenerPorIdCoordinatorFromOtherGroupAccessPendienteCoordPlan() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(false);
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_COORDINADOR");

        ThesisPlanResponse response = service.obtenerPorId(12);

        assertNotNull(response);
        assertEquals(12, response.idPlanTesis());
    }

    @Test
    @DisplayName("obtenerPorId - coordinator denied when not in group and tramite is not PENDIENTE_COORDINADOR")
    void obtenerPorIdCoordinatorDeniedWhenNotInGroupAndNotPendienteCoord() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(grupoValidation.esCoordinadorDelGrupo(303L, 2)).thenReturn(false);
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_DIRECCION");

        assertThrows(PlanAccessDeniedException.class, () -> service.obtenerPorId(12));
    }

    @Test
    @DisplayName("obtenerPorId - director can access plan when tramite is PENDIENTE_DIRECCION")
    void obtenerPorIdDirectorCanAccessPendienteDireccionPlan() {
        mockAuthentication(404L, "ROLE_DIRECTOR_INVESTIGACION");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.APROBADO, null, null
        );
        when(planRepository.findById(12)).thenReturn(Optional.of(plan));
        when(tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(12)).thenReturn("PENDIENTE_DIRECCION");

        ThesisPlanResponse response = service.obtenerPorId(12);

        assertNotNull(response);
        assertEquals(12, response.idPlanTesis());
    }

    @Test
    @DisplayName("listarPorEstudiante - coordinator sees all plans for given student id (non-decano path)")
    void listarPorEstudianteCoordinatorSeesAllPlansForGivenStudentId() {
        mockAuthentication(303L, "ROLE_COORDINADOR_GRUPO");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 200L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findByEstudiante(200L)).thenReturn(List.of(plan));

        List<ThesisPlanResponse> list = service.listarPorEstudiante(200L);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("resolverIdEstudianteSegunRol - student uses own id regardless of param")
    void resolverIdEstudianteSegunRolStudentUsesOwnId() {
        mockAuthentication(101L, "ROLE_ESTUDIANTE");
        ThesisPlan plan = new ThesisPlan(
                12, "AI Thesis", "Abstract", 101L, 1, 2, 99,
                ThesisPlanStatus.POSTULADO, null, null
        );
        when(planRepository.findByEstudiante(101L)).thenReturn(List.of(plan));

        List<ThesisPlanResponse> list = service.listarPorEstudiante(999L);

        assertNotNull(list);
        assertEquals(1, list.size());
        verify(planRepository).findByEstudiante(101L);
    }
}
