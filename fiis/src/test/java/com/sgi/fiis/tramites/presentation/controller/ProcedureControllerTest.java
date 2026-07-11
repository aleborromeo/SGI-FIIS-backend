package com.sgi.fiis.tramites.presentation.controller;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.tramites.application.dto.*;
import com.sgi.fiis.tramites.application.usecase.*;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcedureController - Presentation layer")
class ProcedureControllerTest {

    @Mock private CreateProcedureUseCase createProcedureUseCase;
    @Mock private ApproveProcedureUseCase approveProcedureUseCase;
    @Mock private FlagProcedureUseCase flagProcedureUseCase;
    @Mock private RemediateProcedureUseCase remediateProcedureUseCase;
    @Mock private RejectProcedureUseCase rejectProcedureUseCase;
    @Mock private RegisterResolutionUseCase registerResolutionUseCase;
    @Mock private GetTraceabilityUseCase getTraceabilityUseCase;

    @InjectMocks
    private ProcedureController controller;

    private CustomUserDetails coordinator;
    private CustomUserDetails student;

    @BeforeEach
    void setUp() {
        coordinator = new CustomUserDetails(10L, "coord@unas.edu.pe", "pwd", true,
                List.of(new SimpleGrantedAuthority("ROLE_COORDINADOR_GRUPO")));
        student = new CustomUserDetails(5L, "est@unas.edu.pe", "pwd", true,
                List.of(new SimpleGrantedAuthority("ROLE_ESTUDIANTE")));
    }

    @Test
    @DisplayName("create: sets applicant id from JWT, not from request body")
    void create_setsApplicantIdFromJwt_returns201() {
        ProcedureRequestDto dto = ProcedureRequestDto.builder()
                .procedureType(ProcedureType.PLAN_TESIS)
                .thesisReferenceId(1L)
                .build();
        ProcedureResponseDto expected = ProcedureResponseDto.builder()
                .id(1L)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .build();
        when(createProcedureUseCase.execute(any())).thenReturn(expected);

        ResponseEntity<ProcedureResponseDto> response = controller.create(dto, student);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(5L, dto.getApplicantId()); // security: set from JWT, not HTTP body
        assertSame(expected, response.getBody());
        verify(createProcedureUseCase).execute(dto);
    }

    @Test
    @DisplayName("approve: extracts RoleEnum from JWT and delegates to use case")
    void approve_extractsRoleFromJwt_returns200() {
        ProcedureResponseDto expected = ProcedureResponseDto.builder().id(1L).build();
        when(approveProcedureUseCase.execute(1L, RoleEnum.COORDINADOR_GRUPO, 10L))
                .thenReturn(expected);

        ResponseEntity<ProcedureResponseDto> response = controller.approve(1L, coordinator);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
    }

    @Test
    @DisplayName("approve: user without role throws BusinessException before calling use case")
    void approve_userWithoutRole_throwsBusinessException() {
        CustomUserDetails noRole = new CustomUserDetails(1L, "x@x.com", "pwd", true, List.of());

        assertThrows(BusinessException.class, () -> controller.approve(1L, noRole));
        verifyNoInteractions(approveProcedureUseCase);
    }

    @Test
    @DisplayName("flag: passes observation text to use case")
    void flag_passesObservationTextToUseCase_returns200() {
        FlagProcedureRequestDto body = new FlagProcedureRequestDto("Missing advisor signature");
        ProcedureResponseDto expected = ProcedureResponseDto.builder().id(2L).build();
        when(flagProcedureUseCase.execute(2L, RoleEnum.COORDINADOR_GRUPO, 10L, "Missing advisor signature"))
                .thenReturn(expected);

        ResponseEntity<ProcedureResponseDto> response = controller.flag(2L, body, coordinator);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
    }

    @Test
    @DisplayName("remediate: passes remediation detail and applicant id from JWT")
    void remediate_passesDetailAndApplicantId_returns200() {
        RemediateProcedureRequestDto body = new RemediateProcedureRequestDto("Attached scanned signature");
        ProcedureResponseDto expected = ProcedureResponseDto.builder().id(3L).build();
        when(remediateProcedureUseCase.execute(3L, 5L, "Attached scanned signature"))
                .thenReturn(expected);

        ResponseEntity<ProcedureResponseDto> response = controller.remediate(3L, body, student);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("reject: extracts RoleEnum from JWT and returns 200")
    void reject_extractsRoleFromJwt_returns200() {
        ProcedureResponseDto expected = ProcedureResponseDto.builder().id(4L).build();
        when(rejectProcedureUseCase.execute(4L, RoleEnum.COORDINADOR_GRUPO, 10L))
                .thenReturn(expected);

        ResponseEntity<ProcedureResponseDto> response = controller.reject(4L, coordinator);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("registerResolution: passes only user id from JWT")
    void registerResolution_passesUserIdFromJwt_returns200() {
        ProcedureResponseDto expected = ProcedureResponseDto.builder().id(5L).build();
        when(registerResolutionUseCase.execute(5L, 10L)).thenReturn(expected);

        ResponseEntity<ProcedureResponseDto> response = controller.registerResolution(5L, coordinator);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("getTraceability: returns movement list without role requirement")
    void getTraceability_returnsMovements_noRoleRequired() {
        List<ProcedureMovementResponseDto> movements = List.of(
                ProcedureMovementResponseDto.builder().action("PRESENTADO_POR_SOLICITANTE").build(),
                ProcedureMovementResponseDto.builder().action("APROBADO_COORDINADOR").build()
        );
        when(getTraceabilityUseCase.execute(6L)).thenReturn(movements);

        ResponseEntity<List<ProcedureMovementResponseDto>> response = controller.getTraceability(6L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }
}
