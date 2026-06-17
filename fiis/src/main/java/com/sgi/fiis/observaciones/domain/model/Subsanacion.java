package com.sgi.fiis.observaciones.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una subsanación presentada para levantar una observación.
 */
public class Subsanacion {

    private Integer id;
    private Integer idObservacion;
    private Integer idSolicitante;
    private String descripcion;
    private Integer idDocumentoAdjunto;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;

    public Subsanacion(Integer id, Integer idObservacion, Integer idSolicitante,
                       String descripcion, Integer idDocumentoAdjunto,
                       LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.idObservacion = idObservacion;
        this.idSolicitante = idSolicitante;
        this.descripcion = descripcion;
        this.idDocumentoAdjunto = idDocumentoAdjunto;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
    }

    public static Subsanacion crear(Integer idObservacion, Integer idSolicitante,
                                    String descripcion, Integer idDocumentoAdjunto) {
        LocalDateTime ahora = LocalDateTime.now();
        return new Subsanacion(null, idObservacion, idSolicitante,
                descripcion, idDocumentoAdjunto, ahora, ahora);
    }

    public boolean tieneDocumentoAdjunto() {
        return this.idDocumentoAdjunto != null;
    }

    public Integer getId() { return id; }
    public Integer getIdObservacion() { return idObservacion; }
    public Integer getIdSolicitante() { return idSolicitante; }
    public String getDescripcion() { return descripcion; }
    public Integer getIdDocumentoAdjunto() { return idDocumentoAdjunto; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}
