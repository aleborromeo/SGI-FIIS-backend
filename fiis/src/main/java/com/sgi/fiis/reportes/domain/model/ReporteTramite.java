package com.sgi.fiis.reportes.domain.model;

import java.time.LocalDateTime;

/**
 * DTO de salida para el reporte institucional de trámites (RF-94).
 * Consolida datos de: tramites, usuarios (solicitante) y grupos_investigacion.
 */
public class ReporteTramite {

    private Integer       idTramite;
    private String        codigoTramite;
    private String        tipoTramite;
    private String        nombreSolicitante;
    private String        estadoActual;
    private String        rolRevisorActual;
    private String        nombreGrupo;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaActualizacion;

    public ReporteTramite() {
        // requerido por JdbcTemplate RowMapper
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public Integer       getIdTramite()           { return idTramite; }
    public void          setIdTramite(Integer v)   { this.idTramite = v; }

    public String        getCodigoTramite()           { return codigoTramite; }
    public void          setCodigoTramite(String v)    { this.codigoTramite = v; }

    public String        getTipoTramite()           { return tipoTramite; }
    public void          setTipoTramite(String v)    { this.tipoTramite = v; }

    public String        getNombreSolicitante()           { return nombreSolicitante; }
    public void          setNombreSolicitante(String v)   { this.nombreSolicitante = v; }

    public String        getEstadoActual()           { return estadoActual; }
    public void          setEstadoActual(String v)    { this.estadoActual = v; }

    public String        getRolRevisorActual()           { return rolRevisorActual; }
    public void          setRolRevisorActual(String v)    { this.rolRevisorActual = v; }

    public String        getNombreGrupo()           { return nombreGrupo; }
    public void          setNombreGrupo(String v)    { this.nombreGrupo = v; }

    public LocalDateTime getFechaEnvio()             { return fechaEnvio; }
    public void          setFechaEnvio(LocalDateTime v){ this.fechaEnvio = v; }

    public LocalDateTime getFechaActualizacion()             { return fechaActualizacion; }
    public void          setFechaActualizacion(LocalDateTime v){ this.fechaActualizacion = v; }
}
