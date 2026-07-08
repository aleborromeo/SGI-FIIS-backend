package com.sgi.fiis.convocatorias.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;

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

    private ResearchCall(Builder builder) {
        if (builder.endDate.isBefore(builder.startDate)) {
            throw new BusinessRuleValidationException("convocatorias.error.end-date-before-start");
        }
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.status = builder.status;
        this.documentId = builder.documentId;
        // Almacenamos una lista completamente inmutable en el dominio
        this.researchLineIds = List.copyOf(builder.researchLineIds);
    }

    /** Convenience constructor kept for backward compatibility with existing callers. */
    public ResearchCall(Integer id, String title, String description, LocalDate startDate,
                        LocalDate endDate, CallStatus status, Integer documentId,
                        List<Integer> researchLineIds) {
        this(new Builder()
                .id(id).title(title).description(description)
                .startDate(startDate).endDate(endDate).status(status)
                .documentId(documentId).researchLineIds(researchLineIds));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer id;
        private String title;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private CallStatus status;
        private Integer documentId;
        // Inicializamos la lista vacía por defecto para evitar nulos molestos
        private List<Integer> researchLineIds = new ArrayList<>();

        private Builder() {}

        public Builder id(Integer id)                           { this.id = id; return this; }
        public Builder title(String title)                      { this.title = title; return this; }
        public Builder description(String description)          { this.description = description; return this; }
        public Builder startDate(LocalDate startDate)           { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate)               { this.endDate = endDate; return this; }
        public Builder status(CallStatus status)                { this.status = status; return this; }
        public Builder documentId(Integer documentId)           { this.documentId = documentId; return this; }
        
        // SOLUCIÓN DEFINITIVA PARA DEEPSOURCE
        public Builder researchLineIds(List<Integer> lineIds) { 
            if (lineIds == null) {
                this.researchLineIds = new ArrayList<>();
            } else {
                this.researchLineIds = new ArrayList<>(lineIds); 
            }
            return this; 
        }

        public ResearchCall build() { return new ResearchCall(this); }
    }

    /**
     * Validates whether a project can be submitted on the given date.
     *
     * @param submissionDate the date the project is being submitted
     * @throws BusinessRuleValidationException if the call is not open or the
     * date is outside the submission period
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