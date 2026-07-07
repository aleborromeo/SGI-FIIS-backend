package com.sgi.fiis.tramites.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataProcedureRepository extends JpaRepository<ProcedureEntity, Long> {

    Optional<ProcedureEntity> findByCodigoTramite(String codigoTramite);

    List<ProcedureEntity> findByIdSolicitante(Long idSolicitante);

    List<ProcedureEntity> findByEstadoActual(String estadoActual);

    boolean existsByCodigoTramite(String codigoTramite);
}
