package com.sgi.fiis.lineas_investigacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineaInvestigacion {
    private Integer id;
    private String nombreLinea;
    private boolean esActiva;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public void activar() {
        this.esActiva = true;
        this.fechaActualizacion = LocalDateTime.now(ZoneId.of("UTC"));
    }

    public void desactivar() {
        this.esActiva = false;
        this.fechaActualizacion = LocalDateTime.now(ZoneId.of("UTC"));
    }
}
