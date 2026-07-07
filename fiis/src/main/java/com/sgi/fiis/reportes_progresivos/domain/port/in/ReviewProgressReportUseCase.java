package com.sgi.fiis.reportes_progresivos.domain.port.in;

import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;

/**
 * Driving port -- use case: Review a Progress Report.
 * Covers Coordinator (forward) and Director (approve, observe, reject) actions.
 * RF-74, RF-75
 */
public interface ReviewProgressReportUseCase {

    /** Coordinator forwards the report to the Director (RF-74). */
    ProgressReportResponse forwardToDirector(Long reportId);

    /** Director approves the report (RF-75). */
    ProgressReportResponse approve(Long reportId);

    /** Director observes the report with feedback (RF-75). */
    ProgressReportResponse observe(Long reportId, String observation);

    /** Director rejects the report (RF-75). */
    ProgressReportResponse reject(Long reportId);
}
