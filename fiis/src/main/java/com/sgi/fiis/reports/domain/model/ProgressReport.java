package com.sgi.fiis.reports.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Output DTO for the institutional progress report.
 * Consolidates data from: progress reports (informes_avance), projects, and research groups.
 */
public class ProgressReport {

    private Integer       reportId;
    private String        projectCode;
    private String        projectTitle;
    private String        reportType;
    private String        period;
    private BigDecimal    progressPercentage;
    private String        reportStatus;
    private String        groupName;
    private LocalDateTime registeredAt;

    public ProgressReport() {
        // required by JdbcTemplate RowMapper
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getReportId()            { return reportId; }
    public void          setReportId(Integer v)    { this.reportId = v; }

    public String        getProjectCode()            { return projectCode; }
    public void          setProjectCode(String v)    { this.projectCode = v; }

    public String        getProjectTitle()            { return projectTitle; }
    public void          setProjectTitle(String v)    { this.projectTitle = v; }

    public String        getReportType()            { return reportType; }
    public void          setReportType(String v)    { this.reportType = v; }

    public String        getPeriod()            { return period; }
    public void          setPeriod(String v)    { this.period = v; }

    public BigDecimal    getProgressPercentage()             { return progressPercentage; }
    public void          setProgressPercentage(BigDecimal v) { this.progressPercentage = v; }

    public String        getReportStatus()            { return reportStatus; }
    public void          setReportStatus(String v)    { this.reportStatus = v; }

    public String        getGroupName()            { return groupName; }
    public void          setGroupName(String v)    { this.groupName = v; }

    public LocalDateTime getRegisteredAt()              { return registeredAt; }
    public void          setRegisteredAt(LocalDateTime v){ this.registeredAt = v; }
}
