package com.sgi.fiis.evaluaciones.infrastructure.persistence.mapper;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;
import com.sgi.fiis.evaluaciones.infrastructure.persistence.entity.EvaluacionJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EvaluacionPersistenceMapperTest {

    @Test
    void toJpaEntityDebeConvertirDominioAEntidadJpa() {
        LocalDateTime fechaAsignacion = LocalDateTime.now();
        LocalDateTime fechaEvaluacion = LocalDateTime.now().plusDays(1);

        Evaluacion evaluacion = Evaluacion.reconstruir(
                1L,
                10L,
                null,
                2L,
                ResultadoEvaluacion.APROBADO,
                90,
                "Evaluación correcta.",
                fechaAsignacion,
                fechaEvaluacion
        );

        EvaluacionJpaEntity entity = EvaluacionPersistenceMapper.toJpaEntity(evaluacion);

        assertEquals(1L, entity.getIdEvaluacion());
        assertEquals(10L, entity.getIdProyecto());
        assertNull(entity.getIdPlanTesis());
        assertEquals(2L, entity.getIdEvaluador());
        assertEquals(ResultadoEvaluacion.APROBADO, entity.getResultado());
        assertEquals(90, entity.getPuntaje());
        assertEquals("Evaluación correcta.", entity.getObservaciones());
        assertEquals(fechaAsignacion, entity.getFechaAsignacion());
        assertEquals(fechaEvaluacion, entity.getFechaEvaluacion());
    }

    @Test
    void toDomainDebeConvertirEntidadJpaADominio() {
        LocalDateTime fechaAsignacion = LocalDateTime.now();
        LocalDateTime fechaEvaluacion = LocalDateTime.now().plusDays(1);

        Evaluacion evaluacionOriginal = Evaluacion.reconstruir(
                1L,
                null,
                5L,
                2L,
                ResultadoEvaluacion.CON_OBSERVACIONES,
                70,
                "Debe corregir algunos puntos.",
                fechaAsignacion,
                fechaEvaluacion
        );

        EvaluacionJpaEntity entity = EvaluacionPersistenceMapper.toJpaEntity(evaluacionOriginal);

        Evaluacion evaluacion = EvaluacionPersistenceMapper.toDomain(entity);

        assertEquals(1L, evaluacion.getIdEvaluacion());
        assertNull(evaluacion.getIdProyecto());
        assertEquals(5L, evaluacion.getIdPlanTesis());
        assertEquals(2L, evaluacion.getIdEvaluador());
        assertEquals(ResultadoEvaluacion.CON_OBSERVACIONES, evaluacion.getResultado());
        assertEquals(70, evaluacion.getPuntaje());
        assertEquals("Debe corregir algunos puntos.", evaluacion.getObservaciones());
        assertEquals(fechaAsignacion, evaluacion.getFechaAsignacion());
        assertEquals(fechaEvaluacion, evaluacion.getFechaEvaluacion());
    }
}