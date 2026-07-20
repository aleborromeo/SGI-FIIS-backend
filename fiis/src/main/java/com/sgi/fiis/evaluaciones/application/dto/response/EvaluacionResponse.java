package com.sgi.fiis.evaluaciones.application.dto.response;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;

import java.time.LocalDateTime;

public record EvaluacionResponse(
        Long idEvaluacion,
        Long idProyecto,
        Long idPlanTesis,
        Long idEvaluador,
        ResultadoEvaluacion resultado,
        Integer puntaje,
        String observaciones,
        LocalDateTime fechaAsignacion,
        LocalDateTime fechaEvaluacion,
        boolean pendiente,
        String proyectoTitulo,
        String proyectoResumen,
        String planTesisTitulo,
        String planTesisResumen
) {
    public EvaluacionResponse(
            Long idEvaluacion, Long idProyecto, Long idPlanTesis, Long idEvaluador,
            ResultadoEvaluacion resultado, Integer puntaje, String observaciones,
            LocalDateTime fechaAsignacion, LocalDateTime fechaEvaluacion, boolean pendiente
    ) {
        this(idEvaluacion, idProyecto, idPlanTesis, idEvaluador, resultado, puntaje,
                observaciones, fechaAsignacion, fechaEvaluacion, pendiente,
                null, null, null, null);
    }
}