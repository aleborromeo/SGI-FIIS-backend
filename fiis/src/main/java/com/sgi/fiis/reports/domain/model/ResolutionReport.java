package com.sgi.fiis.reports.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Output DTO for the institutional resolution report.
 * Consolidates data from: resolutions, procedures, and users (applicant).
 */
public class ResolutionReport {

    private Integer       resolutionId;
    private String        resolutionNumber;
    private LocalDate     issueDate;
    private String        subject;
    private String        procedureCode;
    private String        procedureType;
    private String        applicantName;
    private LocalDateTime registeredAt;

    public ResolutionReport() {
        // required by JdbcTemplate RowMapper
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getResolutionId()            { return resolutionId; }
    public void          setResolutionId(Integer v)    { this.resolutionId = v; }

    public String        getResolutionNumber()            { return resolutionNumber; }
    public void          setResolutionNumber(String v)    { this.resolutionNumber = v; }

    public LocalDate     getIssueDate()             { return issueDate; }
    public void          setIssueDate(LocalDate v)  { this.issueDate = v; }

    public String        getSubject()           { return subject; }
    public void          setSubject(String v)    { this.subject = v; }

    public String        getProcedureCode()           { return procedureCode; }
    public void          setProcedureCode(String v)    { this.procedureCode = v; }

    public String        getProcedureType()           { return procedureType; }
    public void          setProcedureType(String v)    { this.procedureType = v; }

    public String        getApplicantName()           { return applicantName; }
    public void          setApplicantName(String v)   { this.applicantName = v; }

    public LocalDateTime getRegisteredAt()              { return registeredAt; }
    public void          setRegisteredAt(LocalDateTime v){ this.registeredAt = v; }
}
