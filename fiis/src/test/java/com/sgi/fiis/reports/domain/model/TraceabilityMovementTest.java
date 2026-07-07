package com.sgi.fiis.reports.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TraceabilityMovement}.
 * Verifies that the default constructor and all getters/setters work correctly.
 */
@DisplayName("TraceabilityMovement - Unit Tests")
class TraceabilityMovementTest {

    @Test
    @DisplayName("Default constructor should create empty instance without errors")
    void constructor_createsEmptyInstance() {
        TraceabilityMovement m = new TraceabilityMovement();
        assertThat(m).isNotNull();
    }

    @Test
    @DisplayName("Getters and setters should work correctly for all fields")
    void gettersAndSetters_workCorrectly() {
        TraceabilityMovement m = new TraceabilityMovement();

        LocalDateTime movementDate = LocalDateTime.of(2026, Month.MAY, 20, 10, 30);

        m.setMovementId(1);
        m.setProcedureId(100);
        m.setProcedureCode("TRM-100");
        m.setActionUserName("Juan Perez");
        m.setAction("APROBAR");
        m.setPreviousStatus("EN_PROGRESO");
        m.setNewStatus("APROBADO");
        m.setObservation("Todo conforme");
        m.setMovementDate(movementDate);

        assertThat(m.getMovementId()).isEqualTo(1);
        assertThat(m.getProcedureId()).isEqualTo(100);
        assertThat(m.getProcedureCode()).isEqualTo("TRM-100");
        assertThat(m.getActionUserName()).isEqualTo("Juan Perez");
        assertThat(m.getAction()).isEqualTo("APROBAR");
        assertThat(m.getPreviousStatus()).isEqualTo("EN_PROGRESO");
        assertThat(m.getNewStatus()).isEqualTo("APROBADO");
        assertThat(m.getObservation()).isEqualTo("Todo conforme");
        assertThat(m.getMovementDate()).isEqualTo(movementDate);
    }

    @Test
    @DisplayName("All fields should be null in newly created instance")
    void nullFields_onNewInstance() {
        TraceabilityMovement m = new TraceabilityMovement();

        assertThat(m.getMovementId()).isNull();
        assertThat(m.getProcedureId()).isNull();
        assertThat(m.getProcedureCode()).isNull();
        assertThat(m.getActionUserName()).isNull();
        assertThat(m.getAction()).isNull();
        assertThat(m.getPreviousStatus()).isNull();
        assertThat(m.getNewStatus()).isNull();
        assertThat(m.getObservation()).isNull();
        assertThat(m.getMovementDate()).isNull();
    }
}
