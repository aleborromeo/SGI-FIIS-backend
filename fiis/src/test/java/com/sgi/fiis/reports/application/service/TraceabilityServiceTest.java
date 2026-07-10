package com.sgi.fiis.reports.application.service;

import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
import com.sgi.fiis.reports.domain.repository.TraceabilityRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TraceabilityService}.
 * Uses Mockito to isolate the service from the database (RF-96 to RF-99).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TraceabilityService - Unit Tests")
class TraceabilityServiceTest {

    @Mock
    private TraceabilityRepositoryPort repo;

    @InjectMocks
    private TraceabilityService service;

    private TraceabilityMovement movement1;
    private TraceabilityMovement movement2;

    @BeforeEach
    void setUp() {
        movement1 = new TraceabilityMovement();
        movement1.setMovementId(1);
        movement1.setProcedureId(10);
        movement1.setProcedureCode("TRM-2024-001");
        movement1.setActionUserName("Juan Pérez");
        movement1.setAction("CREACION");
        movement1.setPreviousStatus(null);
        movement1.setNewStatus("PENDIENTE");
        movement1.setObservation("Trámite creado");
        movement1.setMovementDate(LocalDateTime.of(2024, Month.JANUARY, 15, 9, 0));

        movement2 = new TraceabilityMovement();
        movement2.setMovementId(2);
        movement2.setProcedureId(10);
        movement2.setProcedureCode("TRM-2024-001");
        movement2.setActionUserName("María García");
        movement2.setAction("APROBACION");
        movement2.setPreviousStatus("PENDIENTE");
        movement2.setNewStatus("APROBADO");
        movement2.setObservation("Revisado y aprobado");
        movement2.setMovementDate(LocalDateTime.of(2024, Month.JANUARY, 16, 14, 30));
    }

    // -------------------------------------------------------------------------
    // RF-96: Successful query with multiple movements
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("RF-96: Should return complete history of a procedure with movements")
    void getTraceability_withMovements_returnsList() {
        // arrange
        when(repo.findByProcedureId(10)).thenReturn(List.of(movement1, movement2));

        // act
        List<TraceabilityMovement> result = service.getTraceability(10);

        // assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAction()).isEqualTo("CREACION");
        assertThat(result.get(1).getAction()).isEqualTo("APROBACION");
        verify(repo, times(1)).findByProcedureId(10);
    }

    // -------------------------------------------------------------------------
    // RF-97: Procedure without movements -> empty list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("RF-97: Should return empty list when procedure has no movements")
    void getTraceability_noMovements_returnsEmptyList() {
        // arrange
        when(repo.findByProcedureId(999)).thenReturn(Collections.emptyList());

        // act
        List<TraceabilityMovement> result = service.getTraceability(999);

        // assert
        assertThat(result).isEmpty();
        verify(repo, times(1)).findByProcedureId(999);
    }

    // -------------------------------------------------------------------------
    // RF-98: Verify repository is called exactly once
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("RF-98: Should delegate query to repository exactly once")
    void getTraceability_shouldInvokeRepositoryOnce() {
        // arrange
        when(repo.findByProcedureId(anyInt())).thenReturn(Collections.emptyList());

        // act
        service.getTraceability(5);

        // assert
        verify(repo, times(1)).findByProcedureId(5);
        verifyNoMoreInteractions(repo);
    }

    // -------------------------------------------------------------------------
    // RF-99: Verify complete fields of returned movement
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("RF-99: Should return complete fields of movement correctly")
    void getTraceability_verifiesMovementFields() {
        // arrange
        when(repo.findByProcedureId(10)).thenReturn(List.of(movement1));

        // act
        List<TraceabilityMovement> result = service.getTraceability(10);

        // assert
        TraceabilityMovement mov = result.get(0);
        assertThat(mov.getMovementId()).isEqualTo(1);
        assertThat(mov.getProcedureId()).isEqualTo(10);
        assertThat(mov.getProcedureCode()).isEqualTo("TRM-2024-001");
        assertThat(mov.getActionUserName()).isEqualTo("Juan Pérez");
        assertThat(mov.getAction()).isEqualTo("CREACION");
        assertThat(mov.getPreviousStatus()).isNull();
        assertThat(mov.getNewStatus()).isEqualTo("PENDIENTE");
        assertThat(mov.getObservation()).isEqualTo("Trámite creado");
        assertThat(mov.getMovementDate()).isEqualTo(LocalDateTime.of(2024, Month.JANUARY, 15, 9, 0));
    }

    // -------------------------------------------------------------------------
    // RNF-46: Procedure with a single movement
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("RNF-46: Should correctly handle procedure with a single movement")
    void getTraceability_singleMovement_returnsListOfOne() {
        // arrange
        when(repo.findByProcedureId(10)).thenReturn(List.of(movement1));

        // act
        List<TraceabilityMovement> result = service.getTraceability(10);

        // assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMovementId()).isEqualTo(1);
    }

    // -------------------------------------------------------------------------
    // RNF-47: Procedure with non-existent ID should return empty list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("RNF-47: Should return empty list for non-existent procedure ID")
    void getTraceability_nonExistentId_returnsEmptyList() {
        // arrange
        when(repo.findByProcedureId(Integer.MAX_VALUE)).thenReturn(Collections.emptyList());

        // act
        List<TraceabilityMovement> result = service.getTraceability(Integer.MAX_VALUE);

        // assert
        assertThat(result).isNotNull().isEmpty();
    }
}
