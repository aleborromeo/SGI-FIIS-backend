package com.sgi.fiis.reportes_progresivos.application.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Input command DTO for amending an observed Progress Report.
 * RF-76, RN-08
 */
@Getter
@Setter
public class AmendReportCommand {

    private Long reportId;
    private Long requesterId;              // authenticated researcher
    private Long amendmentDocumentId;      // document with corrections (RF-69)
}

