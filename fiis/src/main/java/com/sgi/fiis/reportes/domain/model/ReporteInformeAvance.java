package com.sgi.fiis.reportes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de salida para el reporte institucional de informes de avance (RF-94).
 * Consolida datos de: informes_avance, proyectos y grupos_investigacion.
 */
public class ReporteInformeAvance {

    private Integer       idInforme;
    private String        codigoProyecto;
    private String        tituloProyecto;
    private String        tipoInforme;
    private String        periodo;
    private BigDecimal    porcentajeAvance;
    private String        estadoInforme;
    private String        nombreGrupo;
    private LocalDateTime fechaRegistro;

    public ReporteInformeAvance() {}

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getIdInforme()            { return idInforme; }
    public void          setIdInforme(Integer v)    { this.idInforme = v; }

    public String        getCodigoProyecto()            { return codigoProyecto; }
    public void          setCodigoProyecto(String v)    { this.codigoProyecto = v; }

    public String        getTituloProyecto()            { return tituloProyecto; }
    public void          setTituloProyecto(String v)    { this.tituloProyecto = v; }

    public String        getTipoInforme()            { return tipoInforme; }
    public void          setTipoInforme(String v)    { this.tipoInforme = v; }

    public String        getPeriodo()            { return periodo; }
    public void          setPeriodo(String v)    { this.periodo = v; }

    public BigDecimal    getPorcentajeAvance()             { return porcentajeAvance; }
    public void          setPorcentajeAvance(BigDecimal v) { this.porcentajeAvance = v; }

    public String        getEstadoInforme()            { return estadoInforme; }
    public void          setEstadoInforme(String v)    { this.estadoInforme = v; }

    public String        getNombreGrupo()            { return nombreGrupo; }
    public void          setNombreGrupo(String v)    { this.nombreGrupo = v; }

    public LocalDateTime getFechaRegistro()              { return fechaRegistro; }
    public void          setFechaRegistro(LocalDateTime v){ this.fechaRegistro = v; }
}
