package com.sgi.fiis.convocatorias.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCallRequest {

    @NotBlank
    private String title;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
