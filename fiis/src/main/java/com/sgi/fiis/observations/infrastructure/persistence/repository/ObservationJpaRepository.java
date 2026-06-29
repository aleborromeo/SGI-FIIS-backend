package com.sgi.fiis.observations.infrastructure.persistence.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sgi.fiis.observations.infrastructure.persistence.entity.ObservationJpaEntity;

/**
 * Spring Data JPA Repository for {@link ObservationJpaEntity}.
 */
@Repository
public interface ObservationJpaRepository extends JpaRepository<ObservationJpaEntity, Integer> {

    /**
     * Finds all observations of a procedure ordered by creation date descending.
     */
    List<ObservationJpaEntity> findByProcedureIdOrderByCreatedAtDesc(Integer procedureId);

    /**
     * Finds pending observations of a procedure.
     */
    List<ObservationJpaEntity> findByProcedureIdAndStatus(Integer procedureId, String status);
}
