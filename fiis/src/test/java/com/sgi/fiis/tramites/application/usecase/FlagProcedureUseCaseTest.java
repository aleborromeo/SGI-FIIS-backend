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
class FlagProcedureUseCaseTest {

    @Mock
    private ProcedureRepositoryPort tramiteRepositoryPort;

    @Mock
    private ProcedureEventPublisherPort eventPublisherPort;

    @InjectMocks
    private FlagProcedureUseCase flagProcedureUseCase;

    private Procedure buildProcedure(ProcedureStatus status, RoleEnum rolRevisor) {
        return Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-000001")
                .tipoTramite(ProcedureType.PLAN_TESIS)
                .idSolicitante(2L)
                .idGrupo(10L)
                .estadoActual(status)
                .rolRevisorActual(rolRevisor)
                .idReferenciaTesis(200L)
                .fechaEnvio(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_coordinadorObserva_retornaObservado() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(tramiteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = flagProcedureUseCase.execute(1L, RoleEnum.COORDINADOR_GRUPO, 10L, "Falta firma");

        assertEquals(ProcedureStatus.OBSERVADO, result.getEstadoActual());
        verify(eventPublisherPort).publishProcedureFlagged(any());
    }

    @Test
    void execute_directorObserva_retornaObservado() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_DIRECCION, RoleEnum.DIRECTOR_INVESTIGACION);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(tramiteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = flagProcedureUseCase.execute(1L, RoleEnum.DIRECTOR_INVESTIGACION, 20L, "Falta anexo");

        assertEquals(ProcedureStatus.OBSERVADO, result.getEstadoActual());
        verify(eventPublisherPort).publishProcedureFlagged(any());
    }

    @Test
    void execute_decanoObserva_retornaObservado() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_DECANATO, RoleEnum.DECANO);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(tramiteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = flagProcedureUseCase.execute(1L, RoleEnum.DECANO, 30L, "Falta resolución previa");

        assertEquals(ProcedureStatus.OBSERVADO, result.getEstadoActual());
        verify(eventPublisherPort).publishProcedureFlagged(any());
    }

    @Test
    void execute_rolInvalido_lanzaBusinessException() {
        Procedure tramite = buildProcedure(ProcedureStatus.PENDIENTE_COORDINADOR, RoleEnum.COORDINADOR_GRUPO);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));

        assertThrows(BusinessException.class,
                () -> flagProcedureUseCase.execute(1L, RoleEnum.EVALUADOR, 99L, "texto"));
        verify(tramiteRepositoryPort, never()).save(any());
    }

    @Test
    void execute_tramiteNoEncontrado_lanzaResourceNotFoundException() {
        when(tramiteRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> flagProcedureUseCase.execute(999L, RoleEnum.COORDINADOR_GRUPO, 10L, "texto"));
    }
}
