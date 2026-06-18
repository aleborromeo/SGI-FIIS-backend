package com.sgi.fiis.tramites.application.mapper;

import com.sgi.fiis.tramites.application.dto.MovimientoResponseDto;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.domain.model.MovimientoTramite;
import com.sgi.fiis.tramites.domain.model.Tramite;

import java.util.List;
import java.util.stream.Collectors;

public class TramiteMapper {

    private TramiteMapper() {}

    public static TramiteResponseDto toResponse(Tramite tramite) {
        return TramiteResponseDto.builder()
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

    public static MovimientoResponseDto toMovimientoResponse(MovimientoTramite movimiento) {
        return MovimientoResponseDto.builder()
                .idUsuarioAccion(movimiento.getIdUsuarioAccion())
                .accion(movimiento.getAccion())
                .estadoAnterior(movimiento.getEstadoAnterior())
                .estadoNuevo(movimiento.getEstadoNuevo())
                .observacion(movimiento.getObservacion())
                .fechaMovimiento(movimiento.getFechaMovimiento())
                .build();
    }

    public static List<MovimientoResponseDto> toMovimientoResponseList(List<MovimientoTramite> movimientos) {
        return movimientos.stream()
                .map(TramiteMapper::toMovimientoResponse)
                .collect(Collectors.toList());
    }
}
