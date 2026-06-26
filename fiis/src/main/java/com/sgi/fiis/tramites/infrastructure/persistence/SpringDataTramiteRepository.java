package com.sgi.fiis.tramites.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataTramiteRepository extends JpaRepository<TramiteEntity, Long> {

    Optional<TramiteEntity> findByCodigoTramite(String codigoTramite);

    List<TramiteEntity> findByIdSolicitante(Long idSolicitante);

    List<TramiteEntity> findByEstadoActual(String estadoActual);

    boolean existsByCodigoTramite(String codigoTramite);
}
