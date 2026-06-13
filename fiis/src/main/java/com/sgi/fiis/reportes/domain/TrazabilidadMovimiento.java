package com.sgi.fiis.reportes.domain;

import java.time.LocalDateTime;

/**
 * DTO de salida para la trazabilidad cronológica de un trámite (RF-96 a RF-99).
 * Mapea la tabla movimientos_tramite con información del usuario que actuó.
 */
public class TrazabilidadMovimiento {

    private Integer       idMovimiento;
    private Integer       idTramite;
    private String        codigoTramite;
    private String        nombreUsuarioAccion;
    private String        accion;
    private String        estadoAnterior;
    private String        estadoNuevo;
    private String        observacion;
    private LocalDateTime fechaMovimiento;

    public TrazabilidadMovimiento() {}

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getIdMovimiento()            { return idMovimiento; }
    public void          setIdMovimiento(Integer v)    { this.idMovimiento = v; }

    public Integer       getIdTramite()            { return idTramite; }
    public void          setIdTramite(Integer v)    { this.idTramite = v; }

    public String        getCodigoTramite()            { return codigoTramite; }
    public void          setCodigoTramite(String v)    { this.codigoTramite = v; }

    public String        getNombreUsuarioAccion()            { return nombreUsuarioAccion; }
    public void          setNombreUsuarioAccion(String v)    { this.nombreUsuarioAccion = v; }

    public String        getAccion()            { return accion; }
    public void          setAccion(String v)    { this.accion = v; }

    public String        getEstadoAnterior()            { return estadoAnterior; }
    public void          setEstadoAnterior(String v)    { this.estadoAnterior = v; }

    public String        getEstadoNuevo()            { return estadoNuevo; }
    public void          setEstadoNuevo(String v)    { this.estadoNuevo = v; }

    public String        getObservacion()            { return observacion; }
    public void          setObservacion(String v)    { this.observacion = v; }

    public LocalDateTime getFechaMovimiento()              { return fechaMovimiento; }
    public void          setFechaMovimiento(LocalDateTime v){ this.fechaMovimiento = v; }
}
