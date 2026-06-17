package com.sgi.fiis.observaciones.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Entidad de dominio que representa una observación registrada sobre un trámite.
 */
@Getter
@Builder
public class Observacion {

    private Integer id;
    private Integer idTramite;
    private Integer idRevisor;
    private TipoObservacion tipoObservacion;
    private String descripcion;
    private ObservacionEstado estado;
    private String rolRevisor;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    public static Observacion crear(Integer idTramite, Integer idRevisor,
                                    TipoObservacion tipoObservacion,
                                    String descripcion, String rolRevisor) {
        LocalDateTime ahora = LocalDateTime.now(ZoneId.systemDefault());
        return Observacion.builder()
                .idTramite(idTramite)
                .idRevisor(idRevisor)
                .tipoObservacion(tipoObservacion)
                .descripcion(descripcion)
                .estado(ObservacionEstado.PENDIENTE)
                .rolRevisor(rolRevisor)
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();
    }

    public boolean marcarSubsanada() {
        if (this.estado != ObservacionEstado.PENDIENTE) {
            return false;
        }
        this.estado = ObservacionEstado.SUBSANADA;
        this.fechaActualizacion = LocalDateTime.now(ZoneId.systemDefault());
        return true;
    }

    public String determinarRolRetorno() {
        switch (this.rolRevisor) {
            case "DECANO":
                return "DIRECTOR_INVESTIGACION";
            case "DIRECTOR_INVESTIGACION":
                return "COORDINADOR_GRUPO";
            case "COORDINADOR_GRUPO":
            default:
                return null;
        }
    }

    public boolean esSubsanable() {
        return this.estado == ObservacionEstado.PENDIENTE;
    }
}

