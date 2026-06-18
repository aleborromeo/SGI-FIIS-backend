package com.sgi.fiis.observaciones.infrastructure.persistence.adapter;

import com.sgi.fiis.observaciones.domain.model.Observation;
import com.sgi.fiis.observaciones.domain.port.ObservationRepositoryPort;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.ObservationEntity;
import com.sgi.fiis.observaciones.infrastructure.persistence.repository.ObservationJpaRepository;
import com.sgi.fiis.observaciones.infrastructure.persistence.mapper.ObservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ObservationRepositoryAdapter implements ObservationRepositoryPort {

    private final ObservationJpaRepository jpaRepository;
    private final ObservationMapper mapper;

    @Override
    public Observation save(Observation observation) {
        ObservationEntity entity = mapper.toEntity(observation);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Observation> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Observation> findByProcedureId(Integer procedureId) {
        return jpaRepository.findByProcedureIdOrderByRegisteredAtDesc(procedureId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Observation> findPendingByProcedureId(Integer procedureId) {
        return jpaRepository.findByProcedureIdAndStatus(procedureId, "PENDING")
                .stream().map(mapper::toDomain).toList();
    }
}
