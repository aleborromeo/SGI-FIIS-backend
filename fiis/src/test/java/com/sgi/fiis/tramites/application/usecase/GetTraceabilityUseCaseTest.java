package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureMovementResponseDto;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.tramites.domain.port.ProcedureMovementRepositoryPort;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTraceabilityUseCaseTest {

    @Mock
    private ProcedureRepositoryPort tramiteRepositoryPort;

    @Mock
    private ProcedureMovementRepositoryPort movimientoRepositoryPort;

    @InjectMocks
    private GetTraceabilityUseCase getTraceabilityUseCase;

    @Test
    void execute_tramiteExiste_retornaListaMovimientos() {
        Procedure tramite = Procedure.builder()
                .id(1L)
                .tipoTramite(ProcedureType.PROYECTO)
                .estadoActual(ProcedureStatus.PENDIENTE_COORDINADOR)
                .idSolicitante(2L)
                .build();
        ProcedureMovement mov = ProcedureMovement.builder()
                .idUsuarioAccion(2L)
                .accion("PRESENTADO_POR_SOLICITANTE")
                .estadoAnterior(ProcedureStatus.REGISTRADO)
                .estadoNuevo(ProcedureStatus.PENDIENTE_COORDINADOR)
                .fechaMovimiento(LocalDateTime.now())
                .build();

        when(tramiteRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(movimientoRepositoryPort.findByProcedureId(1L)).thenReturn(List.of(mov));

        List<ProcedureMovementResponseDto> result = getTraceabilityUseCase.execute(1L);

        assertEquals(1, result.size());
        assertEquals("PRESENTADO_POR_SOLICITANTE", result.get(0).getAccion());
    }

    @Test
    void execute_tramiteConMultiplesMovimientos_retornaListaCompleta() {
        Procedure tramite = Procedure.builder()
                .id(2L)
                .tipoTramite(ProcedureType.PLAN_TESIS)
                .estadoActual(ProcedureStatus.PENDIENTE_DIRECCION)
                .idSolicitante(3L)
                .build();
        List<ProcedureMovement> movimientos = List.of(
                ProcedureMovement.builder().accion("PRESENTADO_POR_SOLICITANTE")
                        .estadoAnterior(ProcedureStatus.REGISTRADO)
                        .estadoNuevo(ProcedureStatus.PENDIENTE_COORDINADOR)
                        .fechaMovimiento(LocalDateTime.now()).build(),
                ProcedureMovement.builder().accion("APROBADO_POR_COORDINADOR")
                        .estadoAnterior(ProcedureStatus.PENDIENTE_COORDINADOR)
                        .estadoNuevo(ProcedureStatus.PENDIENTE_DIRECCION)
                        .fechaMovimiento(LocalDateTime.now()).build()
        );

        when(tramiteRepositoryPort.findById(2L)).thenReturn(Optional.of(tramite));
        when(movimientoRepositoryPort.findByProcedureId(2L)).thenReturn(movimientos);

        List<ProcedureMovementResponseDto> result = getTraceabilityUseCase.execute(2L);

        assertEquals(2, result.size());
    }

    @Test
    void execute_tramiteNoEncontrado_lanzaResourceNotFoundException() {
        when(tramiteRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> getTraceabilityUseCase.execute(999L));
    }
}
