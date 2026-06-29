package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RejectProcedureUseCaseTest {

    @Mock
    private ProcedureRepositoryPort tramiteRepositoryPort;

    @InjectMocks
    private RejectProcedureUseCase rejectProcedureUseCase;

    private Procedure buildProcedure(ProcedureStatus status, RoleEnum rolRevisor) {
        return Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-000001")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(2L)
                .idGrupo(10L)
                .estadoActual(status)
                .rolRevisorActual(rolRevisor)
                .idReferenciaProyecto(100L)
                .fechaEnvio(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_coordinadorRechaza_retornaRechazado() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(tramiteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = rejectProcedureUseCase.execute(1L, RoleEnum.COORDINADOR_GRUPO, 10L);

        assertEquals(ProcedureStatus.RECHAZADO, result.getEstadoActual());
    }

    @Test
    void execute_directorRechaza_retornaRechazado() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_DIRECCION, RoleEnum.DIRECTOR_INVESTIGACION);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(tramiteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = rejectProcedureUseCase.execute(1L, RoleEnum.DIRECTOR_INVESTIGACION, 20L);

        assertEquals(ProcedureStatus.RECHAZADO, result.getEstadoActual());
    }

    @Test
    void execute_rolInvalido_lanzaBusinessException() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));

        assertThrows(BusinessException.class,
                () -> rejectProcedureUseCase.execute(1L, RoleEnum.DECANO, 99L));
        verify(tramiteRepositoryPort, never()).save(any());
    }

    @Test
    void execute_tramiteNoEncontrado_lanzaResourceNotFoundException() {
        when(tramiteRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> rejectProcedureUseCase.execute(999L, RoleEnum.COORDINADOR_GRUPO, 10L));
    }
}
