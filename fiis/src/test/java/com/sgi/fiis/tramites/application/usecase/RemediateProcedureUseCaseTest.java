package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.InvalidTransitionException;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
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
class RemediateProcedureUseCaseTest {

    @Mock
    private ProcedureRepositoryPort tramiteRepositoryPort;

    @InjectMocks
    private RemediateProcedureUseCase remediateProcedureUseCase;

    private Procedure buildObservedProcedure(Long idSolicitante) {
        return Procedure.builder()
                .id(1L)
                .codigoTramite("TRM-2026-000001")
                .tipoTramite(ProcedureType.PLAN_TESIS)
                .idSolicitante(idSolicitante)
                .idGrupo(10L)
                .estadoActual(ProcedureStatus.OBSERVADO)
                .rolRevisorActual(null)
                .idReferenciaTesis(200L)
                .fechaEnvio(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    @Test
    void execute_solicitanteOriginalSubsana_retornaPendienteCoordinador() {
        Procedure tramite = buildObservedProcedure(5L);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(tramiteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ProcedureResponseDto result = remediateProcedureUseCase.execute(1L, 5L, "Adjunto firma escaneada");

        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, result.getEstadoActual());
    }

    @Test
    void execute_solicitanteIncorrecto_lanzaInvalidTransitionException() {
        Procedure tramite = buildObservedProcedure(5L);
        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));

        assertThrows(InvalidTransitionException.class,
                () -> remediateProcedureUseCase.execute(1L, 99L, "intento no autorizado"));
        verify(tramiteRepositoryPort, never()).save(any());
    }

    @Test
    void execute_tramiteNoEncontrado_lanzaResourceNotFoundException() {
        when(tramiteRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> remediateProcedureUseCase.execute(999L, 5L, "texto"));
    }
}
