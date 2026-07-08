package com.sgi.fiis.evaluaciones.infrastructure.persistence.adapter;

import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;
import com.sgi.fiis.evaluaciones.domain.ports.out.EvaluacionRepositoryPort;
import com.sgi.fiis.evaluaciones.infrastructure.persistence.entity.EvaluacionJpaEntity;
import com.sgi.fiis.evaluaciones.infrastructure.persistence.mapper.EvaluacionPersistenceMapper;
import com.sgi.fiis.evaluaciones.infrastructure.persistence.repository.EvaluacionJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EvaluacionPersistenceAdapter implements EvaluacionRepositoryPort {

    private final EvaluacionJpaRepository evaluacionJpaRepository;

    public EvaluacionPersistenceAdapter(EvaluacionJpaRepository evaluacionJpaRepository) {
        this.evaluacionJpaRepository = evaluacionJpaRepository;
    }

    @Override
    public Evaluacion guardar(Evaluacion evaluacion) {
        EvaluacionJpaEntity entity = EvaluacionPersistenceMapper.toJpaEntity(evaluacion);
        EvaluacionJpaEntity entityGuardada = evaluacionJpaRepository.save(entity);

        return EvaluacionPersistenceMapper.toDomain(entityGuardada);
    }

    @Override
    public Optional<Evaluacion> buscarPorId(Long idEvaluacion) {
        return evaluacionJpaRepository.findById(idEvaluacion)
                .map(EvaluacionPersistenceMapper::toDomain);
    }

    @Override
    public List<Evaluacion> listarPorEvaluador(Long idEvaluador) {
        return evaluacionJpaRepository.findByIdEvaluador(idEvaluador)
                .stream()
                .map(EvaluacionPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Evaluacion> listarTodas() {
        return evaluacionJpaRepository.findAll()
                .stream()
                .map(EvaluacionPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existeEvaluacionPendienteParaProyecto(Long idProyecto, Long idEvaluador) {
        return evaluacionJpaRepository
                .existsByIdProyectoAndIdEvaluadorAndResultadoIsNullAndFechaEvaluacionIsNull(
                        idProyecto,
                        idEvaluador
                );
    }

    @Override
    public boolean existeEvaluacionPendienteParaPlanTesis(Long idPlanTesis, Long idEvaluador) {
        return evaluacionJpaRepository
                .existsByIdPlanTesisAndIdEvaluadorAndResultadoIsNullAndFechaEvaluacionIsNull(
                        idPlanTesis,
                        idEvaluador
                );
    }
}