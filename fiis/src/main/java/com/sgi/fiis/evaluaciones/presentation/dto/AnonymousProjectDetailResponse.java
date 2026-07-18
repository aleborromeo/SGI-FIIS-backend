package com.sgi.fiis.evaluaciones.presentation.dto;

import java.util.List;

// skipcq: JAVA-W1035
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
    public static AnonymousProjectDetailResponse placeholder(String expedienteCode) {
        return new AnonymousProjectDetailResponse(
                expedienteCode,
                "Convocatoria " + java.time.Year.now(java.time.ZoneId.of("America/Lima")).getValue(),
                "Título del expediente (información anónima)",
                "Resumen técnico del proyecto de investigación.",
                "Objetivo general del proyecto.",
                List.of("Objetivo específico 1", "Objetivo específico 2"),
                50000.0,
                12,
                List.of(
                        new CronogramaItem("Actividad 1", "2026-01-01", "2026-03-01"),
                        new CronogramaItem("Actividad 2", "2026-03-01", "2026-06-01")
                ),
                List.of(
                        new DocumentoItem("documento.pdf", "PDF", "/api/documentos/1")
                ),
                List.of(
                        new CriterioItem(1L, "Pertinencia", "Pertinencia del tema", 20, 0.2),
                        new CriterioItem(2L, "Metodología", "Metodología propuesta", 30, 0.3),
                        new CriterioItem(3L, "Impacto", "Impacto esperado", 25, 0.25),
                        new CriterioItem(4L, "Viabilidad", "Viabilidad del proyecto", 25, 0.25)
                )
        );
    }

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
