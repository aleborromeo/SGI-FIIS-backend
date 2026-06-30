package com.sgi.fiis.reportes_progresivos;

import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportType;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.application.dto.CreateReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.AmendReportCommand;

import java.math.BigDecimal;

/**
 * Shared test utility helper to instantiate domains and commands,
 * reducing duplication across progress report tests.
 */
public class ProgressReportTestHelper {

    public static ProgressReport createReport(Long id, Long projectId, ProgressReportStatus status) {
        ProgressReport report = new ProgressReport(
                projectId, ProgressReportType.PARTIAL, "2026-I",
                new BigDecimal("50.00"), "Achievements", "Difficulties", "Recommendations"
        );
        report.setId(id);
        if (status == ProgressReportStatus.UNDER_REVIEW) {
            report.submitForReview();
        } else if (status == ProgressReportStatus.APPROVED) {
            report.submitForReview();
            report.approve();
        } else if (status == ProgressReportStatus.OBSERVED) {
            report.submitForReview();
            report.observe();
        } else if (status == ProgressReportStatus.REJECTED) {
            report.submitForReview();
            report.reject();
        }
        return report;
    }

    public static ProgressReport createDefaultReport() {
        return createReport(1L, 10L, ProgressReportStatus.PENDING);
    }

    public static CreateReportCommand createCommand() {
        CreateReportCommand cmd = new CreateReportCommand();
        cmd.setProjectId(10L);
        cmd.setRequesterId(20L);
        cmd.setGroupId(30L);
        cmd.setReportType(ProgressReportType.PARTIAL);
        cmd.setPeriod("2026-I");
        cmd.setProgressPercentage(new BigDecimal("50.00"));
        cmd.setAchievements("Achievements");
        cmd.setDifficulties("Difficulties");
        cmd.setRecommendations("Recommendations");
        return cmd;
    }

    public static AmendReportCommand createAmendCommand(Long reportId) {
        AmendReportCommand cmd = new AmendReportCommand();
        cmd.setReportId(reportId);
        cmd.setRequesterId(20L);
        cmd.setAmendmentDocumentId(55L);
        return cmd;
    }

    public static com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse createResponse(Long id, Long projectId, ProgressReportStatus status) {
        com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse res = new com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse();
        res.setId(id);
        res.setProjectId(projectId);
        res.setReportType(ProgressReportType.PARTIAL);
        res.setPeriod("2026-I");
        res.setProgressPercentage(new BigDecimal("30.00"));
        res.setReportStatus(status);
        return res;
    }
}
