package com.sgi.fiis.tramites.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MovimientoTramite {

    private final Long idUsuarioAccion;
    private final String accion;
    private final EstadoTramite estadoAnterior;
    private final EstadoTramite estadoNuevo;
    private final String observacion;
    private final LocalDateTime fechaMovimiento;
}
