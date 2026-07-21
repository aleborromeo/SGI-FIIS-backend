package com.sgi.fiis.evaluaciones.presentation.dto;

import java.util.List;

public record EvaluarEvaluacionRequest(
        Long evaluatorId,
        List<CriterioScore> criteriaScores,
        Integer totalScore,
        String observations,
        String recommendations,
        String dictamen
) {
    public record CriterioScore(
            Long criterionId,
            String criterionName,
            Integer score,
            Integer maxScore,
            String observations
    ) {}
}
