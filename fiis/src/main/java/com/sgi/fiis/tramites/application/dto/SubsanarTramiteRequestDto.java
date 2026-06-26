package com.sgi.fiis.tramites.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubsanarTramiteRequestDto {

    @NotBlank
    private String detalleSubsanacion;
}
