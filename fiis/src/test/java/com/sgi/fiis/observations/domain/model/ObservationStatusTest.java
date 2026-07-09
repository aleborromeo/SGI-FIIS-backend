package com.sgi.fiis.observations.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ObservationStatus Enum Unit Tests")
class ObservationStatusTest {

    @Test
    @DisplayName("Should verify all expected enum values exist")
    void testEnumValues() {
        ObservationStatus[] statuses = ObservationStatus.values();
        assertEquals(3, statuses.length);

        assertEquals(ObservationStatus.PENDIENTE, ObservationStatus.valueOf("PENDIENTE"));
        assertEquals(ObservationStatus.SUBSANADA, ObservationStatus.valueOf("SUBSANADA"));
        assertEquals(ObservationStatus.VIGENTE, ObservationStatus.valueOf("VIGENTE"));
    }

    @Test
    @DisplayName("Should check the correct ordering of enum constants")
    void testEnumOrder() {
        assertEquals(0, ObservationStatus.PENDIENTE.ordinal());
        assertEquals(1, ObservationStatus.SUBSANADA.ordinal());
        assertEquals(2, ObservationStatus.VIGENTE.ordinal());
    }
}
