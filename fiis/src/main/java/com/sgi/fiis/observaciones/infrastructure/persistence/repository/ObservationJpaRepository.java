package com.sgi.fiis.observaciones.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sgi.fiis.observaciones.infrastructure.persistence.entity.ObservationEntity;

/**
 * Spring Data JPA repository for {@link ObservationEntity}.
 */
@Repository
public interface ObservationJpaRepository extends JpaRepository<ObservationEntity, Integer> {

    /**
     * Finds all observations for a procedure, ordered by date descending.
     */
    List<ObservationEntity> findByProcedureIdOrderByRegisteredAtDesc(Integer procedureId);

    /**
     * Finds pending observations for a procedure.
     */
    List<ObservationEntity> findByProcedureIdAndStatus(Integer procedureId, String status);
}
