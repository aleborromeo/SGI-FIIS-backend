package com.sgi.fiis.proyectos.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    @NotBlank(message = "{proyectos.validation.title.required}")
    private String title;

    @NotBlank(message = "{proyectos.validation.summary.required}")
    private String summary;

    @NotBlank(message = "{proyectos.validation.objective.required}")
    private String generalObjective;

    @NotNull(message = "{proyectos.validation.line.required}")
    private Integer researchLineId;

    @NotNull(message = "{proyectos.validation.budget.required}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{proyectos.validation.budget.positive}")
    private BigDecimal budget;

    @NotNull(message = "{proyectos.validation.start-date.required}")
    private LocalDate startDate;

    @NotNull(message = "{proyectos.validation.end-date.required}")
    private LocalDate endDate;

    @NotBlank(message = "{proyectos.validation.place.required}")
    private String executionPlace;

    @NotNull(message = "{proyectos.validation.responsible.required}")
    private Integer responsibleId;

    @NotNull(message = "{proyectos.validation.group.required}")
    private Integer researchGroupId;

    private Integer callId; // Optional if registered outside call

    private Integer documentId; // Optional file metadata ID

    private List<MemberRequest> members; // Optional team members
}