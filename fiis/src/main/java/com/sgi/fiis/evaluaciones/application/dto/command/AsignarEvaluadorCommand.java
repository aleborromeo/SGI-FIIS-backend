package com.sgi.fiis.evaluaciones.application.dto.command;

public record AsignarEvaluadorCommand(
        Long idProyecto,
        Long idPlanTesis,
        Long idEvaluador
) {
}