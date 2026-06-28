package com.sgi.fiis.reportes_progresivos.application.dto;

import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportType;

import java.math.BigDecimal;

/**
 * Input command DTO for creating a Progress Report.
 * Travels from the REST controller to the use case (Application Layer).
 * RF-70, RF-71, RF-72
 */
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

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }

    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }

    public ProgressReportType getReportType() { return reportType; }
    public void setReportType(ProgressReportType reportType) { this.reportType = reportType; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public BigDecimal getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(BigDecimal progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getAchievements() { return achievements; }
    public void setAchievements(String achievements) { this.achievements = achievements; }

    public String getDifficulties() { return difficulties; }
    public void setDifficulties(String difficulties) { this.difficulties = difficulties; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public Long getAttachedDocumentId() { return attachedDocumentId; }
    public void setAttachedDocumentId(Long attachedDocumentId) { this.attachedDocumentId = attachedDocumentId; }
}
