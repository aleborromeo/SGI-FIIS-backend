package com.sgi.fiis.reportes_progresivos.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Domain entity for Progress Reports (RF-70 to RF-77).
 * Pure domain -- no JPA or Spring annotations.
 */
public class ProgressReport {

    private Long id;
    private Long projectId;
    private ProgressReportType reportType;
    private String period;
    private BigDecimal progressPercentage; // 0.00 - 100.00
    private String achievements;
    private String difficulties;
    private String recommendations;
    private Long attachedDocumentId; // nullable (RF-72)
    private ProgressReportStatus reportStatus;
    private LocalDateTime registrationDate;
    private LocalDateTime lastUpdatedDate;

    public ProgressReport(Long projectId, ProgressReportType reportType, String period,
                          BigDecimal progressPercentage, String achievements,
                          String difficulties, String recommendations) {
        validatePercentage(progressPercentage);
        this.projectId          = projectId;
        this.reportType         = reportType;
        this.period             = period;
        this.progressPercentage = progressPercentage;
        this.achievements       = achievements;
        this.difficulties       = difficulties;
        this.recommendations    = recommendations;
        this.reportStatus       = ProgressReportStatus.PENDING;
        this.registrationDate   = LocalDateTime.now(ZoneId.systemDefault());
        this.lastUpdatedDate    = LocalDateTime.now(ZoneId.systemDefault());
    }

    public ProgressReport() {}

    // -- Domain behaviors --

    /** Coordinator forwards to Director (RF-74). */
    public void forwardToDirector() {
        requireStatus(ProgressReportStatus.UNDER_REVIEW, "forward to director");
        this.lastUpdatedDate = LocalDateTime.now(ZoneId.systemDefault());
    }

    /** Director approves (RF-75). */
    public void approve() {
        requireStatus(ProgressReportStatus.UNDER_REVIEW, "approve");
        this.reportStatus   = ProgressReportStatus.APPROVED;
        this.lastUpdatedDate = LocalDateTime.now(ZoneId.systemDefault());
    }

    /** Director observes (RF-75). */
    public void observe() {
        requireStatus(ProgressReportStatus.UNDER_REVIEW, "observe");
        this.reportStatus   = ProgressReportStatus.OBSERVED;
        this.lastUpdatedDate = LocalDateTime.now(ZoneId.systemDefault());
    }

    /** Director rejects (RF-75). */
    public void reject() {
        requireStatus(ProgressReportStatus.UNDER_REVIEW, "reject");
        this.reportStatus   = ProgressReportStatus.REJECTED;
        this.lastUpdatedDate = LocalDateTime.now(ZoneId.systemDefault());
    }

    /** Researcher amends and resubmits (RF-76, RN-08). */
    public void amend() {
        requireStatus(ProgressReportStatus.OBSERVED, "amend");
        this.reportStatus   = ProgressReportStatus.UNDER_REVIEW;
        this.lastUpdatedDate = LocalDateTime.now(ZoneId.systemDefault());
    }

    /** Attach or update the report document (RF-72). */
    public void attachDocument(Long documentId) {
        if (documentId == null) {
            throw new IllegalArgumentException("Document id must not be null.");
        }
        this.attachedDocumentId = documentId;
        this.lastUpdatedDate   = LocalDateTime.now(ZoneId.systemDefault());
    }

    /** Formal submission PENDIENTE -> EN_REVISION (RF-73). */
    public void submitForReview() {
        requireStatus(ProgressReportStatus.PENDING, "submit for review");
        this.reportStatus   = ProgressReportStatus.UNDER_REVIEW;
        this.lastUpdatedDate = LocalDateTime.now(ZoneId.systemDefault());
    }

    // -- Private validations --

    private void validatePercentage(BigDecimal percentage) {
        if (percentage == null
                || percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100.00")) > 0) {
            throw new IllegalArgumentException(
                "Progress percentage must be between 0 and 100. Received: " + percentage);
        }
    }

    private void requireStatus(ProgressReportStatus expected, String action) {
        if (this.reportStatus != expected) {
            throw new IllegalStateException(
                "Cannot " + action + " a report in status " + this.reportStatus
                + ". Expected: " + expected);
        }
    }

    // -- Getters and Setters --

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public ProgressReportType getReportType() { return reportType; }
    public void setReportType(ProgressReportType reportType) { this.reportType = reportType; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public BigDecimal getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(BigDecimal progressPercentage) {
        validatePercentage(progressPercentage);
        this.progressPercentage = progressPercentage;
    }

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
