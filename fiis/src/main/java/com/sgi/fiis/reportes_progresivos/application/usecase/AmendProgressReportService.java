package com.sgi.fiis.reportes_progresivos.application.usecase;

import com.sgi.fiis.reportes_progresivos.application.dto.AmendReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.port.in.AmendProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter.ProgressReportMapper;

/**
 * Service to amend observed progress reports.
 */
public class AmendProgressReportService implements AmendProgressReportUseCase {

    private final ProgressReportRepositoryPort repositoryPort;

    public AmendProgressReportService(ProgressReportRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public ProgressReportResponse amend(AmendReportCommand command) {
        ProgressReport report = repositoryPort.findById(command.getReportId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Progress report not found with id: " + command.getReportId()));

        if (command.getAmendmentDocumentId() != null) {
            report.attachDocument(command.getAmendmentDocumentId());
        }

        report.amend();
        ProgressReport updated = repositoryPort.save(report);
        return ProgressReportMapper.toResponse(updated);
    }
}
