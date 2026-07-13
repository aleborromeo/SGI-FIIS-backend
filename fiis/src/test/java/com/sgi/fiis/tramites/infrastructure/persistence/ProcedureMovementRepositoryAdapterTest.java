package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
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
@SuppressWarnings("all")
class ProcedureMovementRepositoryAdapterTest {

    @Mock private SpringDataProcedureMovementRepository repository;
    @InjectMocks private ProcedureMovementRepositoryAdapter adapter;

    private ProcedureMovementEntity buildMovementEntity() {
        UserEntity actionUser = new UserEntity();
        actionUser.setId(20L);

        ProcedureEntity procedure = new ProcedureEntity();
        procedure.setId(1);

        ProcedureMovementEntity e = new ProcedureMovementEntity();
        e.setProcedure(procedure);
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
        when(repository.findByProcedure_IdOrderByMovementAtAsc(1L)).thenReturn(List.of(buildMovementEntity()));

        List<ProcedureMovement> result = adapter.findByProcedureId(1L);

        assertEquals(1, result.size());
        ProcedureMovement mov = result.get(0);
        assertEquals("APROBADO_POR_COORDINADOR", mov.getAction());
        assertEquals(ProcedureStatus.PENDIENTE_COORDINADOR, mov.getPreviousStatus());
        assertEquals(ProcedureStatus.PENDIENTE_DIRECCION, mov.getNewStatus());
        assertEquals(20L, mov.getActionUserId());
    }

    @Test
    @DisplayName("findByProcedureId: returns empty list when no movements exist")
    void findByProcedureId_emptyList() {
        when(repository.findByProcedure_IdOrderByMovementAtAsc(99L)).thenReturn(List.of());

        assertTrue(adapter.findByProcedureId(99L).isEmpty());
    }
}
