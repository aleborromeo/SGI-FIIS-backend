package com.sgi.fiis.evaluaciones.presentation.dto;

public record AsignarEvaluadorRequest(
        Long idProyecto,
        Long idPlanTesis,
        Long idEvaluador
) {
}