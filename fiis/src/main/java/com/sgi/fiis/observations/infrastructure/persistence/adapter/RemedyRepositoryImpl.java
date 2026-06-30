package com.sgi.fiis.observations.infrastructure.persistence.adapter;

import com.sgi.fiis.observations.domain.model.Remedy;
import com.sgi.fiis.observations.domain.port.RemedyRepository;
import com.sgi.fiis.observations.infrastructure.persistence.entity.RemedyJpaEntity;
import com.sgi.fiis.observations.infrastructure.persistence.repository.RemedyJpaRepository;
import com.sgi.fiis.observations.infrastructure.persistence.mapper.RemedyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RemedyRepositoryImpl implements RemedyRepository {

    private final RemedyJpaRepository jpaRepository;
    private final RemedyMapper mapper;

    @Override
    public Remedy save(Remedy remedy) {
        RemedyJpaEntity entity = mapper.toJpa(remedy);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Remedy> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Remedy> findByObservationId(Integer observationId) {
        return jpaRepository.findByObservationIdOrderByCreatedAtAsc(observationId)
                .stream().map(mapper::toDomain).toList();
    }
}
