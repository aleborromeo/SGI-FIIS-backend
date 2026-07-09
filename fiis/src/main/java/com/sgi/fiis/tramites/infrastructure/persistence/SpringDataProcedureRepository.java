package com.sgi.fiis.tramites.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataProcedureRepository extends JpaRepository<ProcedureEntity, Integer> {
    java.util.Optional<ProcedureEntity> findByCode(String code);
    java.util.List<ProcedureEntity> findByApplicantId(Long id);
    java.util.List<ProcedureEntity> findByStatus(String status);
    boolean existsByCode(String code);
}
