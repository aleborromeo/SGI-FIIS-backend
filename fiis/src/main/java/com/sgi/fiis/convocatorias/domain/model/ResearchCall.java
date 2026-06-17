package com.sgi.fiis.convocatorias.domain.model;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import java.time.LocalDate;

public class ResearchCall {

    private Integer id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private CallStatus status;

    public ResearchCall(Integer id, String title, LocalDate startDate, LocalDate endDate, CallStatus status) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessRuleValidationException("End date cannot be before start date.");
        }
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
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
