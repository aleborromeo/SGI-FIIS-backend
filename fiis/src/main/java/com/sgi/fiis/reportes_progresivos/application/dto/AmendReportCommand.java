package com.sgi.fiis.reportes_progresivos.application.dto;

/**
 * Input command DTO for amending an observed Progress Report.
 * RF-76, RN-08
 */
public class AmendReportCommand {

    private Long reportId;
    private Long requesterId;              // authenticated researcher
    private Long amendmentDocumentId;      // document with corrections (RF-69)

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public Long getAmendmentDocumentId() { return amendmentDocumentId; }
    public void setAmendmentDocumentId(Long amendmentDocumentId) {
        this.amendmentDocumentId = amendmentDocumentId;
    }
}
