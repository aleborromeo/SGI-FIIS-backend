package com.sgi.fiis.evaluaciones.presentation.dto;

import java.util.List;

public record AsignarEvaluadoresRequest(
        Long projectId,
        Long planTesisId,
        List<Long> evaluadorIds
) {
}
