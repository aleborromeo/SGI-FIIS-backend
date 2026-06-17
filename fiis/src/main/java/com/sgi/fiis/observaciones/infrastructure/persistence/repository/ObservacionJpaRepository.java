package com.sgi.fiis.observaciones.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sgi.fiis.observaciones.infrastructure.persistence.entity.ObservacionJpaEntity;

/**
 * Repositorio Spring Data JPA para la entidad {@link ObservacionJpaEntity}.
 */
@Repository
public interface ObservacionJpaRepository extends JpaRepository<ObservacionJpaEntity, Integer> {

    /**
     * Busca todas las observaciones de un trámite, ordenadas por fecha descendente.
     */
    List<ObservacionJpaEntity> findByIdTramiteOrderByFechaRegistroDesc(Integer idTramite);

    /**
     * Busca las observaciones pendientes de un trámite.
     */
    List<ObservacionJpaEntity> findByIdTramiteAndEstadoObservacion(Integer idTramite, String estadoObservacion);
}
