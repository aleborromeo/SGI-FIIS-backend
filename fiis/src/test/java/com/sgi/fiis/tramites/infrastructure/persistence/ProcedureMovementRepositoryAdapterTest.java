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
        ProcedureEntity proc = new ProcedureEntity();
        proc.setId(1);
        e.setProcedure(proc);
        
        com.sgi.fiis.users.infrastructure.persistence.UserEntity actionUser = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
        actionUser.setId(20L);
        e.setActionUser(actionUser);
        
        e.setAction("APROBADO_POR_COORDINADOR");
        e.setPreviousState("PENDIENTE_COORDINADOR");
        e.setNewState("PENDIENTE_DIRECCION");
        e.setMovementAt(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0));
        return e;
    }

    @Test
    @DisplayName("findByProcedureId: maps entity list to domain model")
    void findByProcedureId_returnsMappedList() {
        when(repository.findByProcedureIdOrderByDateAsc(1)).thenReturn(List.of(buildMovementEntity()));

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
        when(repository.findByProcedureIdOrderByDateAsc(99)).thenReturn(List.of());

        assertTrue(adapter.findByProcedureId(99L).isEmpty());
    }

    @Test
    @DisplayName("findByProcedureId: handles null ActionUser correctly")
    void findByProcedureId_nullActionUser() {
        ProcedureMovementEntity e = buildMovementEntity();
        e.setActionUser(null);
        when(repository.findByProcedureIdOrderByDateAsc(2)).thenReturn(List.of(e));

        List<ProcedureMovement> result = adapter.findByProcedureId(2L);

        assertEquals(1, result.size());
        assertNull(result.get(0).getIdUsuarioAccion());
    }
}
