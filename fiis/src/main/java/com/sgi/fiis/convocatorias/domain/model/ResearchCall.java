package com.sgi.fiis.convocatorias.domain.model;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import java.time.LocalDate;
import java.util.List;

/**
 * Domain model representing a research call (convocatoria).
 */
public class ResearchCall {

    private Integer id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private CallStatus status;
    private Integer documentId;
    private List<Integer> researchLineIds;

    /**
     * Constructs a ResearchCall with the given parameters.
     *
     * @param id               unique identifier
     * @param title            call title
     * @param description      call description
     * @param startDate        start date of submission period
     * @param endDate          end date of submission period
     * @param status           current status of the call
     * @param documentId       associated document identifier
     * @param researchLineIds  list of research line identifiers
     * @throws BusinessRuleValidationException if endDate is before startDate
     */
    public ResearchCall(Integer id, String title, String description, LocalDate startDate,
                        LocalDate endDate, CallStatus status, Integer documentId,
                        List<Integer> researchLineIds) {
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
        this.researchLineIds = researchLineIds != null
                ? List.copyOf(researchLineIds)
                : List.of();
    }

    /**
     * Validates whether a project can be submitted on the given date.
     *
     * @param submissionDate the date the project is being submitted
     * @throws BusinessRuleValidationException if the call is not open or the
     *                                         date is outside the submission period
     */
    public void validateCanSubmitProject(LocalDate submissionDate) {
        if (status != CallStatus.OPEN) {
            throw new BusinessRuleValidationException(
                    "Cannot submit projects. The research call is " + status + ".");
        }
        if (submissionDate.isAfter(endDate)) {
            throw new BusinessRuleValidationException(
                    "Cannot submit projects. The submission period closed on " + endDate + ".");
        }
        if (submissionDate.isBefore(startDate)) {
            throw new BusinessRuleValidationException(
                    "Cannot submit projects. The submission period starts on " + startDate + ".");
        }
    }

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

    /**
     * Returns an unmodifiable view of the research line identifiers.
     */
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
