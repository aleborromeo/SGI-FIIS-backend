package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcedureMovementRepositoryAdapter Unit Tests")
class ProcedureMovementRepositoryAdapterTest {

    @Mock private SpringDataProcedureMovementRepository repository;
    @InjectMocks private ProcedureMovementRepositoryAdapter adapter;

    private ProcedureMovementEntity buildMovementEntity() {
        ProcedureMovementEntity e = new ProcedureMovementEntity();
        e.setIdTramite(1L);
        e.setIdUsuarioAccion(20L);
        e.setAccion("APROBADO_POR_COORDINADOR");
        e.setEstadoAnterior("PENDIENTE_COORDINADOR");
        e.setEstadoNuevo("PENDIENTE_DIRECCION");
        e.setFechaMovimiento(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0));
        return e;
    }

    @Test
    @DisplayName("findByProcedureId: maps entity list to domain model")
    void findByProcedureId_returnsMappedList() {
        when(repository.findByProcedureIdOrderByDateAsc(1L)).thenReturn(List.of(buildMovementEntity()));

        List<ProcedureMovement> result = adapter.findByProcedureId(1L);

        assertEquals(1, result.size());
        ProcedureMovement mov = result.get(0);
        assertEquals("APROBADO_POR_COORDINADOR", mov.getAccion());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, mov.getEstadoAnterior());
        assertEquals(ProcedureStatus.PENDIENTE_DIRECCION, mov.getEstadoNuevo());
        assertEquals(20L, mov.getIdUsuarioAccion());
    }

    @Test
    @DisplayName("findByProcedureId: returns empty list when no movements exist")
    void findByProcedureId_emptyList() {
        when(repository.findByProcedureIdOrderByDateAsc(99L)).thenReturn(List.of());

        assertTrue(adapter.findByProcedureId(99L).isEmpty());
    }
}
