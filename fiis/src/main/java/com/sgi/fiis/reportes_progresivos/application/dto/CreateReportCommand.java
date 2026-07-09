package com.sgi.fiis.reportes_progresivos.application.dto;

import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Input command DTO for creating a Progress Report.
 * Travels from the REST controller to the use case (Application Layer).
 * RF-70, RF-71, RF-72
 */
@Getter
@Setter
public class CreateReportCommand {

    private Long projectId;
    private Long requesterId;       // authenticated researcher (extracted from JWT)
    private Long groupId;           // research group of the project (for the automatic procedure)

    private ProgressReportType reportType;
    private String period;
    private BigDecimal progressPercentage;
    private String achievements;
    private String difficulties;
    private String recommendations;

    /** ID of an already-uploaded document via the documents module (RF-72). May be null. */
    private Long attachedDocumentId;
}

