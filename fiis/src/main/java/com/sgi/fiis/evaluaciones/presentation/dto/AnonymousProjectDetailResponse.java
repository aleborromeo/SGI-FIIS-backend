package com.sgi.fiis.evaluaciones.presentation.dto;

import java.util.List;

public record AnonymousProjectDetailResponse(
        String expedienteCode,
        String convocatoria,
        String titulo,
        String resumen,
        String objetivoGeneral,
        List<String> objetivosEspecificos,
        Double presupuestoTotal,
        Integer duracionMeses,
        List<CronogramaItem> cronograma,
        List<DocumentoItem> documentos,
        List<CriterioItem> criterios
) {
    public record CronogramaItem(
            String actividad,
            String fechaInicio,
            String fechaFin
    ) {}

    public record DocumentoItem(
            String nombre,
            String tipo,
            String url
    ) {}

    public record CriterioItem(
            Long id,
            String name,
            String description,
            Integer maxScore,
            Double weight
    ) {}
}
