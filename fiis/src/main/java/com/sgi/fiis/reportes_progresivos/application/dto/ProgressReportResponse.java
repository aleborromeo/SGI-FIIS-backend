package com.sgi.fiis.reportes_progresivos.application.dto;

import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Output DTO (Response) for the Progress Reports module.
 * Used by all use cases that return data to the client.
 * RF-77 (history), RF-44 (visible status)
 */
@Getter
@Setter
public class ProgressReportResponse {

    private Long id;
    private Long projectId;
    private ProgressReportType reportType;
    private String period;
    private BigDecimal progressPercentage;
    private String achievements;
    private String difficulties;
    private String recommendations;
    private Long attachedDocumentId;
    private ProgressReportStatus reportStatus;
    private LocalDateTime registrationDate;
    private LocalDateTime lastUpdatedDate;
}

