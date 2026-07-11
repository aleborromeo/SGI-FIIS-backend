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
    private ProcedureRepositoryPort procedureRepositoryPort;

    @Mock
    private ProcedureMovementRepositoryPort movementRepositoryPort;

    @InjectMocks
    private GetTraceabilityUseCase getTraceabilityUseCase;

    @Test
    void execute_tramiteExiste_retornaListaMovimientos() {
        Procedure tramite = Procedure.builder()
                .id(1L)
                .procedureType(ProcedureType.PROJECT)
                .currentStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .applicantId(2L)
                .build();
        ProcedureMovement mov = ProcedureMovement.builder()
                .actionUserId(2L)
                .action("PRESENTADO_POR_SOLICITANTE")
                .previousStatus(ProcedureStatus.REGISTRADO)
                .newStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                .movementAt(LocalDateTime.now())
                .build();

        when(procedureRepositoryPort.findById(1L)).thenReturn(Optional.of(tramite));
        when(movementRepositoryPort.findByProcedureId(1L)).thenReturn(List.of(mov));

        List<ProcedureMovementResponseDto> result = getTraceabilityUseCase.execute(1L);

        assertEquals(1, result.size());
        assertEquals("PRESENTADO_POR_SOLICITANTE", result.get(0).getAction());
    }

    @Test
    void execute_tramiteConMultiplesMovimientos_retornaListaCompleta() {
        Procedure tramite = Procedure.builder()
                .id(2L)
                .procedureType(ProcedureType.PLAN_TESIS)
                .currentStatus(ProcedureStatus.PENDIENTE_DIRECCION)
                .applicantId(3L)
                .build();
        List<ProcedureMovement> movimientos = List.of(
                ProcedureMovement.builder().action("PRESENTADO_POR_SOLICITANTE")
                        .previousStatus(ProcedureStatus.REGISTRADO)
                        .newStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                        .movementAt(LocalDateTime.now()).build(),
                ProcedureMovement.builder().action("APROBADO_POR_COORDINADOR")
                        .previousStatus(ProcedureStatus.PENDIENTE_COORDINADOR)
                        .newStatus(ProcedureStatus.PENDIENTE_DIRECCION)
                        .movementAt(LocalDateTime.now()).build()
        );

        when(procedureRepositoryPort.findById(2L)).thenReturn(Optional.of(tramite));
        when(movementRepositoryPort.findByProcedureId(2L)).thenReturn(movimientos);

        List<ProcedureMovementResponseDto> result = getTraceabilityUseCase.execute(2L);

        assertEquals(2, result.size());
    }

    @Test
    void execute_tramiteNoEncontrado_lanzaResourceNotFoundException() {
        when(procedureRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> getTraceabilityUseCase.execute(999L));
    }
}
