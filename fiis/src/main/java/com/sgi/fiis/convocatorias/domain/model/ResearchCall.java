package com.sgi.fiis.convocatorias.domain.model;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final List<Integer> researchLineIds;

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
            throw new BusinessRuleValidationException("convocatorias.error.end-date-before-start");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.documentId = documentId;
        this.researchLineIds = researchLineIds != null
                ? new ArrayList<>(researchLineIds)
                : new ArrayList<>();
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
            throw new BusinessRuleValidationException("convocatorias.error.call-not-open", status);
        }
        if (submissionDate.isAfter(endDate)) {
            throw new BusinessRuleValidationException("convocatorias.error.call-closed", endDate);
        }
        if (submissionDate.isBefore(startDate)) {
            throw new BusinessRuleValidationException("convocatorias.error.call-not-started", startDate);
        }
    }

    /** Returns the unique identifier of the call. */
    public Integer getId() {
        return id;
    }

    /** Returns the title of the call. */
    public String getTitle() {
        return title;
    }

    /** Returns the description of the call. */
    public String getDescription() {
        return description;
    }

    /** Returns the associated document identifier. */
    public Integer getDocumentId() {
        return documentId;
    }

    /**
     * Returns a defensive copy of the research line identifiers.
     *
     * @return list of research line IDs
     */
    public List<Integer> getResearchLineIds() {
        return new ArrayList<>(researchLineIds);
    }

    /** Returns the start date of the submission period. */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** Returns the end date of the submission period. */
    public LocalDate getEndDate() {
        return endDate;
    }

    /** Returns the current status of the call. */
    public CallStatus getStatus() {
        return status;
    }

    /** Updates the status of the call. */
    public void setStatus(CallStatus status) {
        this.status = status;
    }
}
