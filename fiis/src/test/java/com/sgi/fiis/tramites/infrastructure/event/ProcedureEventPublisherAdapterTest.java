package com.sgi.fiis.tramites.infrastructure.event;

import com.sgi.fiis.tramites.domain.event.ProcedureApprovedEvent;
import com.sgi.fiis.tramites.domain.event.ProcedureFinalizedEvent;
import com.sgi.fiis.tramites.domain.event.ProcedureFlaggedEvent;
import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("ProcedureEventPublisherAdapter Unit Tests")
class ProcedureEventPublisherAdapterTest {

    private final ProcedureEventPublisherAdapter adapter = new ProcedureEventPublisherAdapter();

    @Test
    @DisplayName("publishProcedureApproved: logs event without throwing")
    void publishProcedureApproved_completesWithoutException() {
        ProcedureApprovedEvent event = ProcedureApprovedEvent.builder()
                .procedureId(1L)
                .code("TRM-2026-001")
                .procedureType(ProcedureType.PROJECT)
                .applicantId(10L)
                .resultingStatus(ProcedureStatus.PENDIENTE_DIRECCION)
                .approverId(20L)
                .approverRole(RoleEnum.COORDINADOR_GRUPO)
                .approvalDate(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0))
                .build();

        assertDoesNotThrow(() -> adapter.publishProcedureApproved(event));
    }

    @Test
    @DisplayName("publishProcedureFlagged: logs event without throwing")
    void publishProcedureFlagged_completesWithoutException() {
        ProcedureFlaggedEvent event = ProcedureFlaggedEvent.builder()
                .procedureId(2L)
                .code("TRM-2026-002")
                .procedureType(ProcedureType.PLAN_TESIS)
                .applicantId(10L)
                .observerId(20L)
                .observerRole(RoleEnum.DIRECTOR_INVESTIGACION)
                .observationText("Falta bibliografía")
                .observationDate(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0))
                .build();

        assertDoesNotThrow(() -> adapter.publishProcedureFlagged(event));
    }

    @Test
    @DisplayName("publishProcedureFinalized: logs event without throwing")
    void publishProcedureFinalized_completesWithoutException() {
        ProcedureFinalizedEvent event = ProcedureFinalizedEvent.builder()
                .procedureId(3L)
                .code("TRM-2026-003")
                .procedureType(ProcedureType.REPORT_AVANCE)
                .applicantId(10L)
                .finalizationDate(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0))
                .build();

        assertDoesNotThrow(() -> adapter.publishProcedureFinalized(event));
    }
}
