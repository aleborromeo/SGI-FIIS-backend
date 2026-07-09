package com.sgi.fiis.observations.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ObservationType Enum Unit Tests")
class ObservationTypeTest {

    @Test
    @DisplayName("Should verify all expected enum values exist")
    void testEnumValues() {
        ObservationType[] types = ObservationType.values();
        assertEquals(4, types.length);

        assertEquals(ObservationType.TECNICA, ObservationType.valueOf("TECNICA"));
        assertEquals(ObservationType.DOCUMENTAL, ObservationType.valueOf("DOCUMENTAL"));
        assertEquals(ObservationType.PRESUPUESTAL, ObservationType.valueOf("PRESUPUESTAL"));
        assertEquals(ObservationType.FORMATO, ObservationType.valueOf("FORMATO"));
    }

    @Test
    @DisplayName("Should check the correct ordering of enum constants")
    void testEnumOrder() {
        assertEquals(0, ObservationType.TECNICA.ordinal());
        assertEquals(1, ObservationType.DOCUMENTAL.ordinal());
        assertEquals(2, ObservationType.PRESUPUESTAL.ordinal());
        assertEquals(3, ObservationType.FORMATO.ordinal());
    }
}
