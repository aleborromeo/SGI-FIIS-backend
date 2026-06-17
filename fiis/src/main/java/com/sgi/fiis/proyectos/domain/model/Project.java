package com.sgi.fiis.proyectos.domain.model;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Project {

    private Integer id;
    private String code;
    private String title;
    private String summary;
    private String generalObjective;
    private Integer researchLineId;
    private String researchLineName;
    private BigDecimal budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private String executionPlace;
    private Long responsibleId;
    private Integer researchGroupId;
    private String researchGroupCode;
    private Integer callId;
    private Integer documentId;
    private ProjectStatus status;

    public Project(Integer id, String code, String title, String summary, String generalObjective,
                   Integer researchLineId, String researchLineName, BigDecimal budget, LocalDate startDate,
                   LocalDate endDate, String executionPlace, Long responsibleId, Integer researchGroupId,
                   String researchGroupCode, Integer callId, Integer documentId, ProjectStatus status) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.summary = summary;
        this.generalObjective = generalObjective;
        this.researchLineId = researchLineId;
        this.researchLineName = researchLineName;
        this.budget = budget;
        this.startDate = startDate;
        this.endDate = endDate;
        this.executionPlace = executionPlace;
        this.responsibleId = responsibleId;
        this.researchGroupId = researchGroupId;
        this.researchGroupCode = researchGroupCode;
        this.callId = callId;
        this.documentId = documentId;
        this.status = status;
    }

    public void validateInvariants() {
        if (budget == null || budget.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleValidationException("Project budget must be greater than zero.");
        }
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new BusinessRuleValidationException("Project start date must be before end date.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessRuleValidationException("Project title is required.");
        }
        // RN-12: GINSOFT group is restricted to 'Computacion' and 'Ingenieria de software' lines
        if ("GINSOFT".equalsIgnoreCase(researchGroupCode)) {
            if (researchLineName != null &&
                !researchLineName.equalsIgnoreCase("Computacion") &&
                !researchLineName.equalsIgnoreCase("Ingenieria de software") &&
                !researchLineName.equalsIgnoreCase("ComputaciÃƒÂ³n") &&
                !researchLineName.equalsIgnoreCase("IngenierÃƒÂ­a de software")) {
                throw new BusinessRuleValidationException("GINSOFT group is strictly restricted to 'Computacion' and 'Ingenieria de software' research lines.");
            }
        }
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getGeneralObjective() { return generalObjective; }
    public void setGeneralObjective(String generalObjective) { this.generalObjective = generalObjective; }

    public Integer getResearchLineId() { return researchLineId; }
    public void setResearchLineId(Integer researchLineId) { this.researchLineId = researchLineId; }

    public String getResearchLineName() { return researchLineName; }
    public void setResearchLineName(String researchLineName) { this.researchLineName = researchLineName; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getExecutionPlace() { return executionPlace; }
    public void setExecutionPlace(String executionPlace) { this.executionPlace = executionPlace; }

    public Long getResponsibleId() { return responsibleId; }
    public void setResponsibleId(Long responsibleId) { this.responsibleId = responsibleId; }

    public Integer getResearchGroupId() { return researchGroupId; }
    public void setResearchGroupId(Integer researchGroupId) { this.researchGroupId = researchGroupId; }

    public String getResearchGroupCode() { return researchGroupCode; }
    public void setResearchGroupCode(String researchGroupCode) { this.researchGroupCode = researchGroupCode; }

    public Integer getCallId() { return callId; }
    public void setCallId(Integer callId) { this.callId = callId; }

    public Integer getDocumentId() { return documentId; }
    public void setDocumentId(Integer documentId) { this.documentId = documentId; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
}
