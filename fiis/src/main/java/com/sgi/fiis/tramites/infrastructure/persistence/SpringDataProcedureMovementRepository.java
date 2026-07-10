package com.sgi.fiis.tramites.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SuppressWarnings("java:S100")
public interface SpringDataProcedureMovementRepository extends JpaRepository<ProcedureMovementEntity, Integer> {

    List<ProcedureMovementEntity> findByProcedure_IdOrderByMovementAtAsc(Long procedureId);

    long countByProcedure_Id(Long procedureId);
}
