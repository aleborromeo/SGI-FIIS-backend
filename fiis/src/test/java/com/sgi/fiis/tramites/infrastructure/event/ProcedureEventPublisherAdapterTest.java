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
                .idTramite(1L)
                .codigoTramite("TRM-2026-001")
                .tipoTramite(ProcedureType.PROYECTO)
                .idSolicitante(10L)
                .estadoResultante(ProcedureStatus.PENDIENTE_DIRECCION)
                .idAprobador(20L)
                .rolAprobador(RoleEnum.COORDINADOR_GRUPO)
                .fechaAprobacion(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0))
                .build();

        assertDoesNotThrow(() -> adapter.publishProcedureApproved(event));
    }

    @Test
    @DisplayName("publishProcedureFlagged: logs event without throwing")
    void publishProcedureFlagged_completesWithoutException() {
        ProcedureFlaggedEvent event = ProcedureFlaggedEvent.builder()
                .idTramite(2L)
                .codigoTramite("TRM-2026-002")
                .tipoTramite(ProcedureType.PLAN_TESIS)
                .idSolicitante(10L)
                .idObservador(20L)
                .rolObservador(RoleEnum.DIRECTOR_INVESTIGACION)
                .textoObservacion("Falta bibliografía")
                .fechaObservacion(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0))
                .build();

        assertDoesNotThrow(() -> adapter.publishProcedureFlagged(event));
    }

    @Test
    @DisplayName("publishProcedureFinalized: logs event without throwing")
    void publishProcedureFinalized_completesWithoutException() {
        ProcedureFinalizedEvent event = ProcedureFinalizedEvent.builder()
                .idTramite(3L)
                .codigoTramite("TRM-2026-003")
                .tipoTramite(ProcedureType.INFORME_AVANCE)
                .idSolicitante(10L)
                .fechaFinalizacion(LocalDateTime.of(2026, Month.JANUARY, 1, 10, 0))
                .build();

        assertDoesNotThrow(() -> adapter.publishProcedureFinalized(event));
    }
}
