package com.sgi.fiis.reports.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Output DTO for the institutional project report.
 * Consolidates data from: projects, research groups, research lines,
 * users (responsible investigator), and calls.
 */
public class ProjectReport {

    private Integer       projectId;
    private String        projectCode;
    private String        projectTitle;
    private String        projectStatus;
    private String        groupName;
    private String        lineName;
    private String        responsibleName;
    private String        callTitle;
    private BigDecimal    budget;
    private LocalDate     startDate;
    private LocalDate     endDate;
    private LocalDateTime createdAt;

    public ProjectReport() {
        // required by JdbcTemplate RowMapper
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getProjectId()         { return projectId; }
    public void          setProjectId(Integer v) { this.projectId = v; }

    public String        getProjectCode()         { return projectCode; }
    public void          setProjectCode(String v)  { this.projectCode = v; }

    public String        getProjectTitle()          { return projectTitle; }
    public void          setProjectTitle(String v)  { this.projectTitle = v; }

    public String        getProjectStatus()           { return projectStatus; }
    public void          setProjectStatus(String v)   { this.projectStatus = v; }

    public String        getGroupName()          { return groupName; }
    public void          setGroupName(String v)   { this.groupName = v; }

    public String        getLineName()          { return lineName; }
    public void          setLineName(String v)   { this.lineName = v; }

    public String        getResponsibleName()          { return responsibleName; }
    public void          setResponsibleName(String v)  { this.responsibleName = v; }

    public String        getCallTitle()          { return callTitle; }
    public void          setCallTitle(String v)  { this.callTitle = v; }

    public BigDecimal    getBudget()           { return budget; }
    public void          setBudget(BigDecimal v){ this.budget = v; }

    public LocalDate     getStartDate()           { return startDate; }
    public void          setStartDate(LocalDate v) { this.startDate = v; }

    public LocalDate     getEndDate()           { return endDate; }
    public void          setEndDate(LocalDate v) { this.endDate = v; }

    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void          setCreatedAt(LocalDateTime v){ this.createdAt = v; }
}
