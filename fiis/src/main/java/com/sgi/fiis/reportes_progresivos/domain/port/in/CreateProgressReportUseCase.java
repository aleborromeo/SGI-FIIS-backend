package com.sgi.fiis.reportes_progresivos.domain.port.in;

import com.sgi.fiis.reportes_progresivos.application.dto.CreateReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;

/**
 * Driving port -- use case: Create a Progress Report.
 * RF-70, RF-71, RF-72, RF-73
 */
public interface CreateProgressReportUseCase {
    ProgressReportResponse create(CreateReportCommand command);
}
