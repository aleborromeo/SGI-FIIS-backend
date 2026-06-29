package com.sgi.fiis.evaluaciones.presentation.dto;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;

public record RegistrarResultadoEvaluacionRequest(
        Long idEvaluador,
        ResultadoEvaluacion resultado,
        Integer puntaje,
        String observaciones
) {
}