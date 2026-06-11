package com.sgi.fiis.evaluaciones.infrastructure.persistence.mapper;

import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;
import com.sgi.fiis.evaluaciones.infrastructure.persistence.entity.EvaluacionJpaEntity;

public class EvaluacionPersistenceMapper {

    private EvaluacionPersistenceMapper() {
    }

    public static EvaluacionJpaEntity toJpaEntity(Evaluacion evaluacion) {
        EvaluacionJpaEntity entity = new EvaluacionJpaEntity();

        entity.setIdEvaluacion(evaluacion.getIdEvaluacion());
        entity.setIdProyecto(evaluacion.getIdProyecto());
        entity.setIdPlanTesis(evaluacion.getIdPlanTesis());
        entity.setIdEvaluador(evaluacion.getIdEvaluador());
        entity.setResultado(evaluacion.getResultado());
        entity.setPuntaje(evaluacion.getPuntaje());
        entity.setObservaciones(evaluacion.getObservaciones());
        entity.setFechaAsignacion(evaluacion.getFechaAsignacion());
        entity.setFechaEvaluacion(evaluacion.getFechaEvaluacion());

        return entity;
    }

    public static Evaluacion toDomain(EvaluacionJpaEntity entity) {
        return Evaluacion.reconstruir(
                entity.getIdEvaluacion(),
                entity.getIdProyecto(),
                entity.getIdPlanTesis(),
                entity.getIdEvaluador(),
                entity.getResultado(),
                entity.getPuntaje(),
                entity.getObservaciones(),
                entity.getFechaAsignacion(),
                entity.getFechaEvaluacion()
        );
    }
}