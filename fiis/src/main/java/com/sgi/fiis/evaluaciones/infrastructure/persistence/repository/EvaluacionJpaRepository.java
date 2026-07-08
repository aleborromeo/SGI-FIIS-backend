package com.sgi.fiis.evaluaciones.infrastructure.persistence.repository;

import com.sgi.fiis.evaluaciones.infrastructure.persistence.entity.EvaluacionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluacionJpaRepository extends JpaRepository<EvaluacionJpaEntity, Long> {

    List<EvaluacionJpaEntity> findByIdEvaluador(Long idEvaluador);

    boolean existsByIdProyectoAndIdEvaluadorAndResultadoIsNullAndFechaEvaluacionIsNull(
            Long idProyecto,
            Long idEvaluador
    );

    boolean existsByIdPlanTesisAndIdEvaluadorAndResultadoIsNullAndFechaEvaluacionIsNull(
            Long idPlanTesis,
            Long idEvaluador
    );
}