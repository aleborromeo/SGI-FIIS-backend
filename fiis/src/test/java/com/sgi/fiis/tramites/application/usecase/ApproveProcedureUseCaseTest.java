package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.port.ProcedureEventPublisherPort;
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
class ApproveProcedureUseCaseTest {

    @Mock
    private ProcedureRepositoryPort tramiteRepositoryPort;

    @Mock
    private ProcedureEventPublisherPort eventPublisherPort;

    @InjectMocks
    private ApproveProcedureUseCase approveProcedureUseCase;

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
    void execute_coordinadorAprueba_retornaPendienteDireccion() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(tramiteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = approveProcedureUseCase.execute(1L, RoleEnum.COORDINADOR_GRUPO, 10L);

        assertEquals(ProcedureStatus.PENDIENTE_DIRECCION, result.getEstadoActual());
        verify(eventPublisherPort).publishProcedureApproved(any());
    }

    @Test
    void execute_directorAprueba_retornaPendienteDecanato() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_DIRECCION, RoleEnum.DIRECTOR_INVESTIGACION);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(tramiteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = approveProcedureUseCase.execute(1L, RoleEnum.DIRECTOR_INVESTIGACION, 20L);

        assertEquals(ProcedureStatus.PENDIENTE_DECANATO, result.getEstadoActual());
        verify(eventPublisherPort).publishProcedureApproved(any());
    }

    @Test
    void execute_rolInvalido_lanzaBusinessException() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));

        assertThrows(BusinessException.class,
                () -> approveProcedureUseCase.execute(1L, RoleEnum.EVALUADOR, 99L));
        verify(tramiteRepositoryPort, never()).save(any());
    }

    @Test
    void execute_tramiteNoEncontrado_lanzaResourceNotFoundException() {
        when(tramiteRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> approveProcedureUseCase.execute(999L, RoleEnum.COORDINADOR_GRUPO, 10L));
    }
}
