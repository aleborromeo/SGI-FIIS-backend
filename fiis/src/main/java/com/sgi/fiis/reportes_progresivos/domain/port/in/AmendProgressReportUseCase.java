package com.sgi.fiis.reportes_progresivos.domain.port.in;

import com.sgi.fiis.reportes_progresivos.application.dto.AmendReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;

/**
 * Driving port -- use case: Amend an observed Progress Report.
 * RF-76, RN-08
 */
public interface AmendProgressReportUseCase {
    ProgressReportResponse amend(AmendReportCommand command);
}
