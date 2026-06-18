package com.sgi.fiis.observaciones.infrastructure.persistence.adapter;

import com.sgi.fiis.observaciones.domain.model.Correction;
import com.sgi.fiis.observaciones.domain.port.CorrectionRepositoryPort;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.CorrectionEntity;
import com.sgi.fiis.observaciones.infrastructure.persistence.repository.CorrectionJpaRepository;
import com.sgi.fiis.observaciones.infrastructure.persistence.mapper.CorrectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CorrectionRepositoryAdapter implements CorrectionRepositoryPort {

    private final CorrectionJpaRepository jpaRepository;
    private final CorrectionMapper mapper;

    @Override
    public Correction save(Correction correction) {
        CorrectionEntity entity = mapper.toEntity(correction);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Correction> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Correction> findByObservationId(Integer observationId) {
        return jpaRepository.findByObservationIdOrderByRegisteredAtAsc(observationId)
                .stream().map(mapper::toDomain).toList();
    }
}
