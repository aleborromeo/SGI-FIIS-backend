package com.sgi.fiis.observations.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvalidRemedyException Unit Tests")
class InvalidRemedyExceptionTest {

    @Test
    @DisplayName("Should create exception with correct observation ID in message")
    void testExceptionMessage() {
        Integer observationId = 123;
        InvalidRemedyException exception = new InvalidRemedyException(observationId);

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("123"));
        assertEquals("No se puede subsanar la observación con ID: 123. Solo se pueden subsanar observaciones en estado PENDIENTE.", exception.getMessage());
    }

    @Test
    @DisplayName("Should be throw-able and catch-able")
    void testThrowException() {
        assertThrows(InvalidRemedyException.class, () -> {
            throw new InvalidRemedyException(456);
        });
    }
}
