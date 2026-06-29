package com.sgi.fiis.tramites.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProcedureMovementRepository extends JpaRepository<ProcedureMovementEntity, Long> {

    @Query("SELECT m FROM ProcedureMovementEntity m WHERE m.idTramite = :id ORDER BY m.fechaMovimiento ASC")
    List<ProcedureMovementEntity> findByProcedureIdOrderByDateAsc(@Param("id") Long id);

    @Query("SELECT COUNT(m) FROM ProcedureMovementEntity m WHERE m.idTramite = :id")
    long countByProcedureId(@Param("id") Long id);
}
