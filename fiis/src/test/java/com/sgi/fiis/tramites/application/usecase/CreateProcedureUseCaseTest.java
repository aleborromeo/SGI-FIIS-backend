package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.ProcedureRequestDto;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProcedureUseCaseTest {

    @Mock
    private ProcedureRepositoryPort procedureRepositoryPort;

    @InjectMocks
    private CreateProcedureUseCase crearTramiteUseCase;

    @Test
    void execute_creaYPresentaTramite_estadoResultanteEsPendienteCoordinador() {
        ProcedureRequestDto dto = ProcedureRequestDto.builder()
                .procedureType(ProcedureType.PROJECT)
                .applicantId(1L)
                .groupId(10L)
                .projectReferenceId(100L)
                .build();

        Procedure tramiteGuardado = Procedure.builder()
                .id(1L)
                .code("TRM-2026-000001")
                .procedureType(ProcedureType.PROJECT)
                .applicantId(1L)
                .groupId(10L)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .currentReviewerRole(RoleEnum.COORDINADOR_GRUPO)
                .projectReferenceId(100L)
                .sentAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(procedureRepositoryPort.save(any(Procedure.class))).thenReturn(tramiteGuardado);

        ProcedureResponseDto resultado = crearTramiteUseCase.execute(dto);

        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, resultado.getCurrentStatus());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, resultado.getCurrentReviewerRole());
        assertEquals(ProcedureType.PROJECT, resultado.getProcedureType());
        verify(procedureRepositoryPort).save(any(Procedure.class));
    }

    @Test
    void execute_tramiteGuardado_tieneTransicionRegistradoAPendienteCoordinador() {
        ProcedureRequestDto dto = ProcedureRequestDto.builder()
                .procedureType(ProcedureType.PLAN_TESIS)
                .applicantId(2L)
                .thesisReferenceId(200L)
                .build();

        when(procedureRepositoryPort.save(any(Procedure.class))).thenAnswer(inv -> inv.getArgument(0));

        crearTramiteUseCase.execute(dto);

        // El tramite fue guardado ya en PENDIENTE_COORDINADOR con un movimiento registrado
        ArgumentCaptor<Procedure> captor = ArgumentCaptor.forClass(Procedure.class);
        verify(procedureRepositoryPort).save(captor.capture());

        Procedure tramiteCapturado = captor.getValue();
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, tramiteCapturado.getCurrentStatus());
        assertEquals(RoleEnum.COORDINADOR_GRUPO, tramiteCapturado.getCurrentReviewerRole());
        assertEquals(1, tramiteCapturado.getMovements().size());
        assertEquals("PRESENTADO_POR_SOLICITANTE", tramiteCapturado.getMovements().get(0).getAction());
    }

    @Test
    void execute_arcoExcluyenteInvalido_lanzaExcepcionSinGuardar() {
        ProcedureRequestDto dto = ProcedureRequestDto.builder()
                .procedureType(ProcedureType.PROJECT)
                .applicantId(1L)
                .projectReferenceId(100L)
                .thesisReferenceId(200L) // dos referencias — inválido
                .build();

        assertThrows(IllegalArgumentException.class, () -> crearTramiteUseCase.execute(dto));
        verify(procedureRepositoryPort, never()).save(any());
    }
}
