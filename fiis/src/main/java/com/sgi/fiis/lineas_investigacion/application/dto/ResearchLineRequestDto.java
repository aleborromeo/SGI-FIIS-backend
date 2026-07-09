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

    @NotBlank(message = "{lineas.validation.nombre.required}")
    @Size(max = 150, message = "{lineas.validation.nombre.max-size}")
    private String lineName;
}
