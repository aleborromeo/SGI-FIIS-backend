package com.sgi.fiis.proyectos.domain.model;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
        if ("GINSOFT".equalsIgnoreCase(researchGroupCode) && researchLineName != null &&
            !researchLineName.equalsIgnoreCase("Computacion") &&
            !researchLineName.equalsIgnoreCase("Ingenieria de software") &&
            !researchLineName.equalsIgnoreCase("ComputaciÃƒÂ³n") &&
            !researchLineName.equalsIgnoreCase("IngenierÃƒÂ­a de software")) {
            
            throw new BusinessRuleValidationException("GINSOFT group is strictly restricted to 'Computacion' and 'Ingenieria de software' research lines.");
        }
    }
}
