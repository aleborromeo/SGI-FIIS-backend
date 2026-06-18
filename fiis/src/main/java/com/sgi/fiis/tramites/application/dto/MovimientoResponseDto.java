package com.sgi.fiis.tramites.application.dto;

import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponseDto {

    private Long idUsuarioAccion;
    private String accion;
    private EstadoTramite estadoAnterior;
    private EstadoTramite estadoNuevo;
    private String observacion;
    private LocalDateTime fechaMovimiento;
}
