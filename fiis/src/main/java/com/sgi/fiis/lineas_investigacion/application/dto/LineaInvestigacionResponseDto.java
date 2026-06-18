package com.sgi.fiis.lineas_investigacion.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineaInvestigacionResponseDto {
    private Integer id;
    private String nombreLinea;
    private boolean esActiva;
    private String fechaCreacion;
    private String fechaActualizacion;
}
