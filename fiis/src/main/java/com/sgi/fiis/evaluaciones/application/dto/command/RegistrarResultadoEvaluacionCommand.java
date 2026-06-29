package com.sgi.fiis.evaluaciones.application.dto.command;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;

public record RegistrarResultadoEvaluacionCommand(
        Long idEvaluacion,
        Long idEvaluador,
        ResultadoEvaluacion resultado,
        Integer puntaje,
        String observaciones
) {
}