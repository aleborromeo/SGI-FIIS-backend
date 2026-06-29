package com.sgi.fiis.reportes_progresivos.domain.port.in;

import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;

import java.util.List;

/**
 * Driving port -- use case: Query Progress Reports.
 * RF-77, RF-42 (visible to the responsible researcher)
 */
public interface QueryProgressReportUseCase {

    /** Full history of reports for a project. */
    List<ProgressReportResponse> listByProject(Long projectId);

    /** Detail of a specific report. */
    ProgressReportResponse getById(Long reportId);
}
