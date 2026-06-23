package com.sgi.fiis.observations.infrastructure.persistence.adapter;

import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.port.ObservationRepository;
import com.sgi.fiis.observations.infrastructure.persistence.entity.ObservationJpaEntity;
import com.sgi.fiis.observations.infrastructure.persistence.repository.ObservationJpaRepository;
import com.sgi.fiis.observations.infrastructure.persistence.mapper.ObservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ObservationRepositoryImpl implements ObservationRepository {

    private final ObservationJpaRepository jpaRepository;
    private final ObservationMapper mapper;

    @Override
    public Observation save(Observation observation) {
        ObservationJpaEntity entity = mapper.toJpa(observation);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Observation> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Observation> findByProcedureId(Integer procedureId) {
        return jpaRepository.findByProcedureIdOrderByCreatedAtDesc(procedureId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Observation> findPendingByProcedureId(Integer procedureId) {
        return jpaRepository.findByProcedureIdAndStatus(procedureId, "PENDIENTE")
                .stream().map(mapper::toDomain).toList();
    }
}
