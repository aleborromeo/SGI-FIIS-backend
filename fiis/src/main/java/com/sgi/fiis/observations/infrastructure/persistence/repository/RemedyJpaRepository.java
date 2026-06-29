package com.sgi.fiis.observations.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sgi.fiis.observations.infrastructure.persistence.entity.RemedyJpaEntity;

/**
 * Spring Data JPA Repository for {@link RemedyJpaEntity}.
 */
public interface RemedyJpaRepository extends JpaRepository<RemedyJpaEntity, Integer> {

    /**
     * Finds all remedies of an observation ordered by registration date.
     */
    List<RemedyJpaEntity> findByObservationIdOrderByCreatedAtAsc(Integer observationId);
}
