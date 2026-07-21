package com.sgi.fiis.evaluaciones.presentation.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsignarEvaluadoresRequestTest {

    @Test
    void shouldCreateRecord() {
        var request = new AsignarEvaluadoresRequest(1L, 2L, List.of(10L, 20L));

        assertEquals(1L, request.projectId());
        assertEquals(2L, request.planTesisId());
        assertEquals(2, request.evaluadorIds().size());
        assertTrue(request.evaluadorIds().contains(10L));
        assertTrue(request.evaluadorIds().contains(20L));
    }
}
