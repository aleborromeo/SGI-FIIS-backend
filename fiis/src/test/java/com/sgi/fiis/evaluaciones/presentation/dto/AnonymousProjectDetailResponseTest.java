package com.sgi.fiis.evaluaciones.presentation.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnonymousProjectDetailResponseTest {

    @Test
    void shouldCreateRecordWithAllFields() {
        var item = new AnonymousProjectDetailResponse.CronogramaItem("Act 1", "2026-01-01", "2026-03-01");
        var doc = new AnonymousProjectDetailResponse.DocumentoItem("doc.pdf", "PDF", "/api/doc/1");
        var crit = new AnonymousProjectDetailResponse.CriterioItem(1L, "Pertinencia", "desc", 20, 0.2);

        var response = new AnonymousProjectDetailResponse(
                "CODE-1", "Conv 2026", "Título", "Resumen", "OG",
                List.of("OE1"), 50000.0, 12,
                List.of(item), List.of(doc), List.of(crit)
        );

        assertEquals("CODE-1", response.expedienteCode());
        assertEquals("Conv 2026", response.convocatoria());
        assertEquals("Título", response.titulo());
        assertEquals("Resumen", response.resumen());
        assertEquals("OG", response.objetivoGeneral());
        assertEquals(1, response.objetivosEspecificos().size());
        assertEquals(50000.0, response.presupuestoTotal());
        assertEquals(12, response.duracionMeses());
        assertEquals(1, response.cronograma().size());
        assertEquals("Act 1", response.cronograma().get(0).actividad());
        assertEquals("2026-01-01", response.cronograma().get(0).fechaInicio());
        assertEquals("2026-03-01", response.cronograma().get(0).fechaFin());
        assertEquals(1, response.documentos().size());
        assertEquals("doc.pdf", response.documentos().get(0).nombre());
        assertEquals("PDF", response.documentos().get(0).tipo());
        assertEquals("/api/doc/1", response.documentos().get(0).url());
        assertEquals(1, response.criterios().size());
        assertEquals(1L, response.criterios().get(0).id());
        assertEquals("Pertinencia", response.criterios().get(0).name());
        assertEquals("desc", response.criterios().get(0).description());
        assertEquals(20, response.criterios().get(0).maxScore());
        assertEquals(0.2, response.criterios().get(0).weight());
    }

    @Test
    void cronogramaItemShouldHandleDates() {
        var item = new AnonymousProjectDetailResponse.CronogramaItem("A", "2026-01-01", "2026-12-31");
        assertEquals("A", item.actividad());
        assertEquals("2026-01-01", item.fechaInicio());
        assertEquals("2026-12-31", item.fechaFin());
    }

    @Test
    void documentoItemShouldHandleFields() {
        var doc = new AnonymousProjectDetailResponse.DocumentoItem("report.pdf", "PDF", "/url");
        assertEquals("report.pdf", doc.nombre());
        assertEquals("PDF", doc.tipo());
        assertEquals("/url", doc.url());
    }

    @Test
    void criterioItemShouldHandleFields() {
        var crit = new AnonymousProjectDetailResponse.CriterioItem(42L, "Criterio", "desc", 10, 0.1);
        assertEquals(42L, crit.id());
        assertEquals("Criterio", crit.name());
        assertEquals("desc", crit.description());
        assertEquals(10, crit.maxScore());
        assertEquals(0.1, crit.weight());
    }
}
