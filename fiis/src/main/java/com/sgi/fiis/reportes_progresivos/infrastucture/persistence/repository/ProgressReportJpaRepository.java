package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.repository;

import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.entity.ProgressReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for ProgressReportEntity.
 * Infrastructure Layer -- extends JpaRepository for CRUD and custom queries.
 */
public interface ProgressReportJpaRepository extends JpaRepository<ProgressReportEntity, Long> {

    /** Find all reports for a project, ordered by registration date descending. */
    List<ProgressReportEntity> findByProjectIdOrderByRegistrationDateDesc(Long projectId);
}
