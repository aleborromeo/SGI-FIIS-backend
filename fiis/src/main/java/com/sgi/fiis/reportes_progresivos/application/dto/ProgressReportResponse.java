package com.sgi.fiis.reportes_progresivos.application.dto;

import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Output DTO (Response) for the Progress Reports module.
 * Used by all use cases that return data to the client.
 * RF-77 (history), RF-44 (visible status)
 */
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

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

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

    public ProgressReportStatus getReportStatus() { return reportStatus; }
    public void setReportStatus(ProgressReportStatus reportStatus) { this.reportStatus = reportStatus; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public LocalDateTime getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }
}
