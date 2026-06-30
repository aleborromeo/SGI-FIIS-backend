package com.sgi.fiis.reportes_progresivos.application.usecase;

import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.port.in.ReviewProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter.ProgressReportMapper;

/**
 * Service to review progress reports.
 */
public class ReviewProgressReportService implements ReviewProgressReportUseCase {

    private final ProgressReportRepositoryPort repositoryPort;

    public ReviewProgressReportService(ProgressReportRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public ProgressReportResponse forwardToDirector(Long reportId) {
        ProgressReport report = findOrFail(reportId);
        report.forwardToDirector();
        ProgressReport updated = repositoryPort.save(report);
        return ProgressReportMapper.toResponse(updated);
    }

    @Override
    public ProgressReportResponse approve(Long reportId) {
        ProgressReport report = findOrFail(reportId);
        report.approve();
        return ProgressReportMapper.toResponse(repositoryPort.save(report));
    }

    @Override
    public ProgressReportResponse observe(Long reportId, String observation) {
        ProgressReport report = findOrFail(reportId);
        report.observe();
        return ProgressReportMapper.toResponse(repositoryPort.save(report));
    }

    @Override
    public ProgressReportResponse reject(Long reportId) {
        ProgressReport report = findOrFail(reportId);
        report.reject();
        return ProgressReportMapper.toResponse(repositoryPort.save(report));
    }

    private ProgressReport findOrFail(Long id) {
        return repositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Progress report not found with id: " + id));
    }
}
