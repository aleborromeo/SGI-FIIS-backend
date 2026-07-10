package com.sgi.fiis.tramites.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100")
public interface SpringDataProcedureRepository extends JpaRepository<ProcedureEntity, Integer> {

    Optional<ProcedureEntity> findByCode(String code);

    List<ProcedureEntity> findByApplicant_Id(Long applicantId);

    List<ProcedureEntity> findByStatus(String status);

    boolean existsByCode(String code);
}
