package com.sgi.fiis.observations.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ObservationNotFoundException Unit Tests")
class ObservationNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with correct observation ID in message")
    void testExceptionMessage() {
        Integer observationId = 999;
        ObservationNotFoundException exception = new ObservationNotFoundException(observationId);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("999"));
        assertEquals("Observación no encontrada con ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Should be throw-able and catch-able")
    void testThrowException() {
        assertThrows(ObservationNotFoundException.class, () -> {
            throw new ObservationNotFoundException(777);
        });
    }
}
