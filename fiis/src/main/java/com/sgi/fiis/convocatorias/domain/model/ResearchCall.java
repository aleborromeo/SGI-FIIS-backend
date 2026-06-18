package com.sgi.fiis.convocatorias.domain.model;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import java.time.LocalDate;
import java.util.List;

public class ResearchCall {

    private Integer id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private CallStatus status;
    private Integer documentId;
    private List<Integer> researchLineIds;

    public ResearchCall(Integer id, String title, String description, LocalDate startDate, LocalDate endDate, CallStatus status, Integer documentId, List<Integer> researchLineIds) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessRuleValidationException("End date cannot be before start date.");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.documentId = documentId;
        this.researchLineIds = researchLineIds;
    }

    public void validateCanSubmitProject(LocalDate submissionDate) {
        if (status != CallStatus.OPEN) {
            throw new BusinessRuleValidationException("Cannot submit projects. The research call is " + status + ".");
        }
        if (submissionDate.isAfter(endDate)) {
            throw new BusinessRuleValidationException("Cannot submit projects. The submission period closed on " + endDate + ".");
        }
        if (submissionDate.isBefore(startDate)) {
            throw new BusinessRuleValidationException("Cannot submit projects. The submission period starts on " + startDate + ".");
        }
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public List<Integer> getResearchLineIds() {
        return researchLineIds;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public CallStatus getStatus() {
        return status;
    }

    public void setStatus(CallStatus status) {
        this.status = status;
    }
}
