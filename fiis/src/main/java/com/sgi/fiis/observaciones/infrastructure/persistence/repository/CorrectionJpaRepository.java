package com.sgi.fiis.observaciones.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sgi.fiis.observaciones.infrastructure.persistence.entity.CorrectionEntity;

/**
 * Spring Data JPA repository for {@link CorrectionEntity}.
 */
@Repository
public interface CorrectionJpaRepository extends JpaRepository<CorrectionEntity, Integer> {

    /**
     * Finds all corrections for an observation, ordered by registration date ascending.
     */
    List<CorrectionEntity> findByObservationIdOrderByRegisteredAtAsc(Integer observationId);
}
