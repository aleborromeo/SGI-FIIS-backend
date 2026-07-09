package com.sgi.fiis.tramites.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProcedureMovementRepository extends JpaRepository<ProcedureMovementEntity, Integer> {

    @Query("SELECT m FROM ProcedureMovementEntity m WHERE m.procedure.id = :id ORDER BY m.movementAt ASC")
    List<ProcedureMovementEntity> findByProcedureIdOrderByDateAsc(@Param("id") int id);

    @Query("SELECT COUNT(m) FROM ProcedureMovementEntity m WHERE m.procedure.id = :id")
    long countByProcedureId(@Param("id") int id);
}
