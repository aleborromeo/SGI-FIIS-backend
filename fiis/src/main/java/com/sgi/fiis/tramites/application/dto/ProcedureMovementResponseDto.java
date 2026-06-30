package com.sgi.fiis.tramites.application.dto;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureMovementResponseDto {

    private Long idUsuarioAccion;
    private String accion;
    private ProcedureStatus estadoAnterior;
    private ProcedureStatus estadoNuevo;
    private String observacion;
    private LocalDateTime fechaMovimiento;
}
