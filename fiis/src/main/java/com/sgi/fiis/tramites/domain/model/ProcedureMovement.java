package com.sgi.fiis.tramites.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcedureMovement {

    private final Long idUsuarioAccion;
    private final String accion;
    private final ProcedureStatus estadoAnterior;
    private final ProcedureStatus estadoNuevo;
    private final String observacion;
    private final LocalDateTime fechaMovimiento;
}
