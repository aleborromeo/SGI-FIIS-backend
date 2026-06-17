package com.sgi.fiis.proyectos.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String summary;

    @NotBlank
    private String generalObjective;

    @NotNull
    private Integer researchLineId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal budget;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotBlank
    private String executionPlace;

    @NotNull
    private Integer responsibleId;

    @NotNull
    private Integer researchGroupId;

    private Integer callId; // Optional if registered outside call

    private Integer documentId; // Optional file metadata ID
}