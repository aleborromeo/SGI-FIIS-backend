package com.sgi.fiis.reportes.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de salida para el reporte institucional de proyectos (RF-94).
 * Consolida datos de: proyectos, grupos_investigacion, lineas_investigacion,
 * usuarios (responsable) y convocatorias.
 */
public class ReporteProyecto {

    private Integer       idProyecto;
    private String        codigoProyecto;
    private String        tituloProyecto;
    private String        estadoProyecto;
    private String        nombreGrupo;
    private String        nombreLinea;
    private String        nombreResponsable;
    private String        tituloConvocatoria;
    private BigDecimal    presupuesto;
    private LocalDate     fechaInicio;
    private LocalDate     fechaFin;
    private LocalDateTime fechaCreacion;

    public ReporteProyecto() {}

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getIdProyecto()         { return idProyecto; }
    public void          setIdProyecto(Integer v) { this.idProyecto = v; }

    public String        getCodigoProyecto()         { return codigoProyecto; }
    public void          setCodigoProyecto(String v)  { this.codigoProyecto = v; }

    public String        getTituloProyecto()          { return tituloProyecto; }
    public void          setTituloProyecto(String v)  { this.tituloProyecto = v; }

    public String        getEstadoProyecto()           { return estadoProyecto; }
    public void          setEstadoProyecto(String v)   { this.estadoProyecto = v; }

    public String        getNombreGrupo()          { return nombreGrupo; }
    public void          setNombreGrupo(String v)   { this.nombreGrupo = v; }

    public String        getNombreLinea()          { return nombreLinea; }
    public void          setNombreLinea(String v)   { this.nombreLinea = v; }

    public String        getNombreResponsable()          { return nombreResponsable; }
    public void          setNombreResponsable(String v)  { this.nombreResponsable = v; }

    public String        getTituloConvocatoria()          { return tituloConvocatoria; }
    public void          setTituloConvocatoria(String v)  { this.tituloConvocatoria = v; }

    public BigDecimal    getPresupuesto()           { return presupuesto; }
    public void          setPresupuesto(BigDecimal v){ this.presupuesto = v; }

    public LocalDate     getFechaInicio()           { return fechaInicio; }
    public void          setFechaInicio(LocalDate v) { this.fechaInicio = v; }

    public LocalDate     getFechaFin()           { return fechaFin; }
    public void          setFechaFin(LocalDate v) { this.fechaFin = v; }

    public LocalDateTime getFechaCreacion()            { return fechaCreacion; }
    public void          setFechaCreacion(LocalDateTime v){ this.fechaCreacion = v; }
}
