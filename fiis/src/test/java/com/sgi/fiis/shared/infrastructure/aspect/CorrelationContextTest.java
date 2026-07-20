package com.sgi.fiis.shared.infrastructure.aspect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CorrelationContextTest {

    private CorrelationContext correlationContext;

    @BeforeEach
    void setUp() {
        correlationContext = new CorrelationContext();
    }

    @Test
    void testCorrelationIdLifecycle() {
        // Initially should be null
        assertNull(correlationContext.getCorrelationId());

        // Set and retrieve
        String correlationId = "test-correlation-123";
        correlationContext.setCorrelationId(correlationId);
        assertEquals(correlationId, correlationContext.getCorrelationId());

        // Clear and check null
        correlationContext.clear();
        assertNull(correlationContext.getCorrelationId());
    }
}
