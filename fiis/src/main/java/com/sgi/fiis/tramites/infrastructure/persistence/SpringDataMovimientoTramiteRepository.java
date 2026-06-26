package com.sgi.fiis.tramites.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataMovimientoTramiteRepository extends JpaRepository<MovimientoTramiteEntity, Long> {

    List<MovimientoTramiteEntity> findByIdTramiteOrderByFechaMovimientoAsc(Long idTramite);

    long countByIdTramite(Long idTramite);
}
