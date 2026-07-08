package com.sgi.fiis.lineas_investigacion.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResearchLineRequestDto {

    @NotBlank(message = "Line name is required")
    @Size(max = 150, message = "Line name must not exceed 150 characters")
    private String lineName;
}
