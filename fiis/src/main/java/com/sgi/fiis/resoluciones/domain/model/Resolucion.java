package com.sgi.fiis.resoluciones.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Resolucion {
    private Long idResolucion;
    private String numeroResolucion;
    private LocalDate fechaEmision;
    private String asunto;
    private Long idTramite;
    private Long idDocumentoAdjunto;
    private LocalDateTime fechaRegistro;

    public Resolucion() {
    }

    public Resolucion(Long idResolucion, String numeroResolucion, LocalDate fechaEmision, String asunto, Long idTramite, Long idDocumentoAdjunto, LocalDateTime fechaRegistro) {
        this.idResolucion = idResolucion;
        this.numeroResolucion = numeroResolucion;
        this.fechaEmision = fechaEmision;
        this.asunto = asunto;
        this.idTramite = idTramite;
        this.idDocumentoAdjunto = idDocumentoAdjunto;
        this.fechaRegistro = fechaRegistro;
    }

    public Long getIdResolucion() {
        return idResolucion;
    }

    public void setIdResolucion(Long idResolucion) {
        this.idResolucion = idResolucion;
    }

    public String getNumeroResolucion() {
        return numeroResolucion;
    }

    public void setNumeroResolucion(String numeroResolucion) {
        this.numeroResolucion = numeroResolucion;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public Long getIdTramite() {
        return idTramite;
    }

    public void setIdTramite(Long idTramite) {
        this.idTramite = idTramite;
    }

    public Long getIdDocumentoAdjunto() {
        return idDocumentoAdjunto;
    }

    public void setIdDocumentoAdjunto(Long idDocumentoAdjunto) {
        this.idDocumentoAdjunto = idDocumentoAdjunto;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
