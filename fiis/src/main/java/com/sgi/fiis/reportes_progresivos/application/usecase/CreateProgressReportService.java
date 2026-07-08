package com.sgi.fiis.reportes_progresivos.application.usecase;

import com.sgi.fiis.reportes_progresivos.application.dto.CreateReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.port.in.CreateProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportEventPort;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter.ProgressReportMapper;

/**
 * Service to register a new Progress Report.
 */
public class CreateProgressReportService implements CreateProgressReportUseCase {

    private final ProgressReportRepositoryPort repositoryPort;
    private final ProgressReportEventPort     procedureEventPort;

    public CreateProgressReportService(ProgressReportRepositoryPort repositoryPort,
                                       ProgressReportEventPort procedureEventPort) {
        this.repositoryPort     = repositoryPort;
        this.procedureEventPort = procedureEventPort;
    }

    @Override
    public ProgressReportResponse create(CreateReportCommand command) {
        ProgressReport report = new ProgressReport(
                command.getProjectId(),
                command.getReportType(),
                command.getPeriod(),
                command.getProgressPercentage(),
                command.getAchievements(),
                command.getDifficulties(),
                command.getRecommendations()
        );

        if (command.getAttachedDocumentId() != null) {
            report.attachDocument(command.getAttachedDocumentId());
        }

        report.submitForReview();
        ProgressReport saved = repositoryPort.save(report);

        procedureEventPort.publishReportProcedure(
                saved.getId(),
                saved.getProjectId(),
                command.getRequesterId(),
                command.getGroupId()
        );

        return ProgressReportMapper.toResponse(saved);
    }
}
