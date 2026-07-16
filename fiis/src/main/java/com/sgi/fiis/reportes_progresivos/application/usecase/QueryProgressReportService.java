package com.sgi.fiis.reportes_progresivos.application.usecase;

import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.port.in.QueryProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter.ProgressReportMapper;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Service to query progress reports.
 */
public class QueryProgressReportService implements QueryProgressReportUseCase {

    private final ProgressReportRepositoryPort repositoryPort;

    public QueryProgressReportService(ProgressReportRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public List<ProgressReportResponse> listByProject(Long projectId) {
        return repositoryPort.findByProjectId(projectId)
                .stream()
                .map(ProgressReportMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProgressReportResponse> listAll() {
        return repositoryPort.findAll()
                .stream()
                .map(ProgressReportMapper::toResponse)
                .toList();
    }

    @Override
    public List<ProgressReportResponse> listByStatus(String status) {
        return repositoryPort.findByStatus(status)
                .stream()
                .map(ProgressReportMapper::toResponse)
                .toList();
    }

    @Override
    public ProgressReportResponse getById(Long reportId) {
        ProgressReport report = repositoryPort.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Progress report not found with id: " + reportId));
        return ProgressReportMapper.toResponse(report);
    }
}
