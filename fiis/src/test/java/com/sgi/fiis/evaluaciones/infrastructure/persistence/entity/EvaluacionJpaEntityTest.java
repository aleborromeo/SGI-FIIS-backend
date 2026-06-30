package com.sgi.fiis.evaluaciones.infrastructure.persistence.entity;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EvaluacionJpaEntityTest {

    @Test
    void gettersYSettersDebenFuncionarCorrectamente() {
        EvaluacionJpaEntity entity = new EvaluacionJpaEntity();

        LocalDateTime fechaAsignacion = LocalDateTime.now();
        LocalDateTime fechaEvaluacion = LocalDateTime.now().plusDays(1);

        entity.setIdEvaluacion(1L);
        entity.setIdProyecto(10L);
        entity.setIdPlanTesis(null);
        entity.setIdEvaluador(2L);
        entity.setResultado(ResultadoEvaluacion.APROBADO);
        entity.setPuntaje(95);
        entity.setObservaciones("Aprobado correctamente.");
        entity.setFechaAsignacion(fechaAsignacion);
        entity.setFechaEvaluacion(fechaEvaluacion);

        assertEquals(1L, entity.getIdEvaluacion());
        assertEquals(10L, entity.getIdProyecto());
        assertNull(entity.getIdPlanTesis());
        assertEquals(2L, entity.getIdEvaluador());
        assertEquals(ResultadoEvaluacion.APROBADO, entity.getResultado());
        assertEquals(95, entity.getPuntaje());
        assertEquals("Aprobado correctamente.", entity.getObservaciones());
        assertEquals(fechaAsignacion, entity.getFechaAsignacion());
        assertEquals(fechaEvaluacion, entity.getFechaEvaluacion());
    }
}