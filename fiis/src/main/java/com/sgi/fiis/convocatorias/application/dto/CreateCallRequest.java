package com.sgi.fiis.convocatorias.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCallRequest {

    @NotBlank(message = "{convocatorias.validation.title.required}")
    private String title;

    @NotBlank(message = "{convocatorias.validation.description.required}")
    private String description;

    @NotNull(message = "{convocatorias.validation.start-date.required}")
    private LocalDate startDate;

    @NotNull(message = "{convocatorias.validation.end-date.required}")
    private LocalDate endDate;

    private Integer documentId;

    @NotEmpty(message = "{convocatorias.validation.lines.not-empty}")
    private List<Integer> researchLineIds;
}
