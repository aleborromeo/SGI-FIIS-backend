package com.sgi.fiis.reportes_progresivos.domain.model;

/**
 * Possible states of a Progress Report.
 * RF-70 to RF-77
 */
public enum ProgressReportStatus {
    PENDING,
    UNDER_REVIEW,
    APPROVED,
    OBSERVED,
    REJECTED
}
