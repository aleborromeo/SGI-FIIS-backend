package com.sgi.fiis.reports.domain.model;

import java.time.LocalDate;

/**
 * Combinable filters for institutional reports.
 * All fields are optional; they are only applied if they are not null.
 */
public class ReportFilter {

    private Integer groupId;
    private String status;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer researcherId;
    private Integer callId;
    private String procedureType;
    private int page;
    private int size;

    public ReportFilter() {
        this.page = 0;
        this.size = 20;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer getGroupId() { return groupId; }
    public void setGroupId(Integer groupId) { this.groupId = groupId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public Integer getResearcherId() { return researcherId; }
    public void setResearcherId(Integer researcherId) { this.researcherId = researcherId; }

    public Integer getCallId() { return callId; }
    public void setCallId(Integer callId) { this.callId = callId; }

    public String getProcedureType() { return procedureType; }
    public void setProcedureType(String procedureType) { this.procedureType = procedureType; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = Math.max(0, page); }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = (size > 0 && size <= 100) ? size : 20; }

    /** Calculated offset for SQL LIMIT/OFFSET. */
    public int getOffset() { return page * size; }
}
