package com.sgi.fiis.reports.domain.model;

import java.time.LocalDateTime;

/**
 * Output DTO for the institutional procedure report.
 * Consolidates data from: procedures, users (applicant), and research groups.
 */
public class ProcedureReport {

    private Integer       procedureId;
    private String        procedureCode;
    private String        procedureType;
    private String        applicantName;
    private String        currentStatus;
    private String        currentReviewerRole;
    private String        groupName;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;

    public ProcedureReport() {
        // required by JdbcTemplate RowMapper
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getProcedureId()           { return procedureId; }
    public void          setProcedureId(Integer v)   { this.procedureId = v; }

    public String        getProcedureCode()           { return procedureCode; }
    public void          setProcedureCode(String v)    { this.procedureCode = v; }

    public String        getProcedureType()           { return procedureType; }
    public void          setProcedureType(String v)    { this.procedureType = v; }

    public String        getApplicantName()           { return applicantName; }
    public void          setApplicantName(String v)   { this.applicantName = v; }

    public String        getCurrentStatus()           { return currentStatus; }
    public void          setCurrentStatus(String v)    { this.currentStatus = v; }

    public String        getCurrentReviewerRole()           { return currentReviewerRole; }
    public void          setCurrentReviewerRole(String v)    { this.currentReviewerRole = v; }

    public String        getGroupName()           { return groupName; }
    public void          setGroupName(String v)    { this.groupName = v; }

    public LocalDateTime getSubmittedAt()             { return submittedAt; }
    public void          setSubmittedAt(LocalDateTime v){ this.submittedAt = v; }

    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void          setUpdatedAt(LocalDateTime v){ this.updatedAt = v; }
}
