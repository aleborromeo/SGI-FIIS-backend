package com.sgi.fiis.reportes_progresivos.domain.port.out;

import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;

import java.util.List;
import java.util.Optional;

/** Output port -- repository abstraction for Progress Reports (RF-70 to RF-77). */
public interface ProgressReportRepositoryPort {

    ProgressReport save(ProgressReport report);

    Optional<ProgressReport> findById(Long id);

    List<ProgressReport> findByProjectId(Long projectId);

    List<ProgressReport> findAll();

    List<ProgressReport> findByStatus(String status);

    boolean existsById(Long id);
}
