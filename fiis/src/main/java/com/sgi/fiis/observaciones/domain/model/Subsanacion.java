package com.sgi.fiis.observaciones.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Entidad de dominio que representa una subsanación presentada para levantar una observación.
 */
@Getter
@Builder
public class Subsanacion {

    private Integer id;
    private Integer idObservacion;
    private Integer idSolicitante;
    private String descripcion;
    private Integer idDocumentoAdjunto;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    public static Subsanacion crear(Integer idObservacion, Integer idSolicitante,
                                    String descripcion, Integer idDocumentoAdjunto) {
        LocalDateTime ahora = LocalDateTime.now(ZoneId.systemDefault());
        return Subsanacion.builder()
                .idObservacion(idObservacion)
                .idSolicitante(idSolicitante)
                .descripcion(descripcion)
                .idDocumentoAdjunto(idDocumentoAdjunto)
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();
    }

    public boolean tieneDocumentoAdjunto() {
        return this.idDocumentoAdjunto != null;
    }
}

