package com.sgi.fiis.observaciones.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sgi.fiis.observaciones.infrastructure.persistence.entity.SubsanacionJpaEntity;

/**
 * Repositorio Spring Data JPA para la entidad {@link SubsanacionJpaEntity}.
 */
@Repository
public interface SubsanacionJpaRepository extends JpaRepository<SubsanacionJpaEntity, Integer> {

    /**
     * Busca todas las subsanaciones de una observación, ordenadas por fecha.
     */
    List<SubsanacionJpaEntity> findByIdObservacionOrderByFechaRegistroAsc(Integer idObservacion);
}
