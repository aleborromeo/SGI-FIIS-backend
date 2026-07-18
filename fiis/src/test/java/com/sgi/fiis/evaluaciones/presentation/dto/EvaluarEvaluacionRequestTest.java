package com.sgi.fiis.evaluaciones.presentation.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvaluarEvaluacionRequestTest {

    @Test
    void shouldCreateRecord() {
        var score = new EvaluarEvaluacionRequest.CriterioScore(1L, "Pertinencia", 18, 20, "Bueno");
        var request = new EvaluarEvaluacionRequest(
                10L, List.of(score), 85, "Ok", "Aprobar", "APROBADO"
        );

        assertEquals(10L, request.evaluatorId());
        assertEquals(1, request.criteriaScores().size());
        assertEquals(85, request.totalScore());
        assertEquals("Ok", request.observations());
        assertEquals("Aprobar", request.recommendations());
        assertEquals("APROBADO", request.dictamen());

        var criterio = request.criteriaScores().get(0);
        assertEquals(1L, criterio.criterionId());
        assertEquals("Pertinencia", criterio.criterionName());
        assertEquals(18, criterio.score());
        assertEquals(20, criterio.maxScore());
        assertEquals("Bueno", criterio.observations());
    }
}
