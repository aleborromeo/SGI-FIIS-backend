package com.sgi.fiis.tramites.application.mapper;

import com.sgi.fiis.tramites.application.dto.ProcedureMovementResponseDto;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.Procedure;

import java.util.List;

public class ProcedureMapper {

    private ProcedureMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ProcedureResponseDto toResponse(Procedure tramite) {
        return ProcedureResponseDto.builder()
                .id(tramite.getId())
                .codigoTramite(tramite.getCodigoTramite())
                .tipoTramite(tramite.getTipoTramite())
                .estadoActual(tramite.getEstadoActual())
                .idSolicitante(tramite.getIdSolicitante())
                .idGrupo(tramite.getIdGrupo())
                .rolRevisorActual(tramite.getRolRevisorActual())
                .observacionActual(tramite.getObservacionActual())
                .idReferenciaProyecto(tramite.getIdReferenciaProyecto())
                .idReferenciaTesis(tramite.getIdReferenciaTesis())
                .idReferenciaInforme(tramite.getIdReferenciaInforme())
                .fechaEnvio(tramite.getFechaEnvio())
                .fechaActualizacion(tramite.getFechaActualizacion())
                .build();
    }

    public static ProcedureMovementResponseDto toMovimientoResponse(ProcedureMovement movimiento) {
        return ProcedureMovementResponseDto.builder()
                .idUsuarioAccion(movimiento.getIdUsuarioAccion())
                .accion(movimiento.getAccion())
                .estadoAnterior(movimiento.getEstadoAnterior())
                .estadoNuevo(movimiento.getEstadoNuevo())
                .observacion(movimiento.getObservacion())
                .fechaMovimiento(movimiento.getFechaMovimiento())
                .build();
    }

    public static List<ProcedureMovementResponseDto> toMovimientoResponseList(List<ProcedureMovement> movimientos) {
        return movimientos.stream()
                .map(ProcedureMapper::toMovimientoResponse)
                .toList();
    }
}
