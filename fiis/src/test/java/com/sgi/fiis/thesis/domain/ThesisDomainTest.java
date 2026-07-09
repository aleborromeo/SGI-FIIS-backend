package com.sgi.fiis.thesis.domain;

import com.sgi.fiis.thesis.domain.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Thesis Domain Unit Tests")
class ThesisDomainTest {

    @Test
    @DisplayName("New ThesisPlan starts in POSTULADO status")
    void newThesisPlanStartsInPostulado() {
        ThesisPlan plan = ThesisPlan.nuevo(
                "Design of AI assistant",
                "Abstract here",
                101L,
                10,
                5,
                123
        );
        assertEquals(ThesisPlanStatus.POSTULADO, plan.getEstadoPlan());
        assertNull(plan.getIdPlanTesis());
        assertEquals("Design of AI assistant", plan.getTituloTesis());
        assertEquals("Abstract here", plan.getResumen());
        assertEquals(101L, plan.getIdEstudiante());
        assertEquals(10, plan.getIdLinea());
        assertEquals(5, plan.getIdGrupo());
        assertEquals(123, plan.getIdDocumentoActual());
    }

    @Test
    @DisplayName("ThesisPlan state transitions - marking observed, approved, rejected")
    void thesisPlanStateTransitions() {
        ThesisPlan plan = new ThesisPlan(
                1, "Title", "Resumen", 101L, 10, 5, 123,
                ThesisPlanStatus.POSTULADO, LocalDateTime.now(), LocalDateTime.now()
        );

        plan.marcarObservado();
        assertEquals(ThesisPlanStatus.OBSERVADO, plan.getEstadoPlan());

        plan.subsanar(124, "Resumen subsanado");
        assertEquals(ThesisPlanStatus.POSTULADO, plan.getEstadoPlan());
        assertEquals(124, plan.getIdDocumentoActual());
        assertEquals("Resumen subsanado", plan.getResumen());

        plan.marcarAprobado();
        assertEquals(ThesisPlanStatus.APROBADO, plan.getEstadoPlan());

        assertThrows(InvalidStateTransitionException.class, plan::marcarRechazado);
    }

    @Test
    @DisplayName("New ThesisReport starts in EN_REVISION status")
    void newThesisReportStartsInEnRevision() {
        ThesisReport report = ThesisReport.nuevo(1, "Final Title", 200);
        assertEquals(ThesisReportStatus.EN_REVISION, report.getEstadoInforme());
        assertEquals(1, report.getIdPlanTesis());
        assertEquals("Final Title", report.getTituloFinal());
        assertEquals(200, report.getIdDocumentoTesis());
    }

    @Test
    @DisplayName("ThesisReport status transition approved and observed")
    void thesisReportTransitions() {
        ThesisReport report = new ThesisReport(
                1, 2, "Final Title", 200, LocalDateTime.now(), ThesisReportStatus.EN_REVISION
        );

        report.aprobar();
        assertEquals(ThesisReportStatus.APROBADO, report.getEstadoInforme());

        assertThrows(InvalidStateTransitionException.class, report::observar);
    }

    @Test
    @DisplayName("enum values and valueOf coverage check")
    void testEnumsCoverage() {
        assertNotNull(ThesisPlanStatus.values());
        assertEquals(ThesisPlanStatus.POSTULADO, ThesisPlanStatus.valueOf("POSTULADO"));

        assertNotNull(ThesisReportStatus.values());
        assertEquals(ThesisReportStatus.EN_REVISION, ThesisReportStatus.valueOf("EN_REVISION"));

        assertNotNull(ThesisProcedureStatus.values());
        assertEquals(ThesisProcedureStatus.PENDIENTE_COORDINADOR, ThesisProcedureStatus.valueOf("PENDIENTE_COORDINADOR"));

        assertNotNull(ThesisProcedureType.values());
        assertEquals(ThesisProcedureType.PLAN_TESIS, ThesisProcedureType.valueOf("PLAN_TESIS"));

        assertNotNull(ReviewerRole.values());
        assertEquals(ReviewerRole.ESTUDIANTE, ReviewerRole.valueOf("ESTUDIANTE"));
    }
}
