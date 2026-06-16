package com.sgi.fiis.reportes.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de salida para el reporte institucional de resoluciones (RF-94).
 * Consolida datos de: resoluciones, tramites y usuarios (solicitante).
 */
public class ReporteResolucion {

    private Integer       idResolucion;
    private String        numeroResolucion;
    private LocalDate     fechaEmision;
    private String        asunto;
    private String        codigoTramite;
    private String        tipoTramite;
    private String        nombreSolicitante;
    private LocalDateTime fechaRegistro;

    public ReporteResolucion() {}

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getIdResolucion()            { return idResolucion; }
    public void          setIdResolucion(Integer v)    { this.idResolucion = v; }

    public String        getNumeroResolucion()            { return numeroResolucion; }
    public void          setNumeroResolucion(String v)    { this.numeroResolucion = v; }

    public LocalDate     getFechaEmision()             { return fechaEmision; }
    public void          setFechaEmision(LocalDate v)  { this.fechaEmision = v; }

    public String        getAsunto()           { return asunto; }
    public void          setAsunto(String v)    { this.asunto = v; }

    public String        getCodigoTramite()           { return codigoTramite; }
    public void          setCodigoTramite(String v)    { this.codigoTramite = v; }

    public String        getTipoTramite()           { return tipoTramite; }
    public void          setTipoTramite(String v)    { this.tipoTramite = v; }

    public String        getNombreSolicitante()           { return nombreSolicitante; }
    public void          setNombreSolicitante(String v)   { this.nombreSolicitante = v; }

    public LocalDateTime getFechaRegistro()              { return fechaRegistro; }
    public void          setFechaRegistro(LocalDateTime v){ this.fechaRegistro = v; }
}
