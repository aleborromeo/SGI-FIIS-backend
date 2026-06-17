package com.sgi.fiis.observaciones.domain.model;

import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa una observación registrada sobre un trámite.
 */
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

    public Observacion(Integer id, Integer idTramite, Integer idRevisor,
                       TipoObservacion tipoObservacion, String descripcion,
                       ObservacionEstado estado, String rolRevisor,
                       LocalDateTime fechaRegistro, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.idTramite = idTramite;
        this.idRevisor = idRevisor;
        this.tipoObservacion = tipoObservacion;
        this.descripcion = descripcion;
        this.estado = estado;
        this.rolRevisor = rolRevisor;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
    }

    public static Observacion crear(Integer idTramite, Integer idRevisor,
                                    TipoObservacion tipoObservacion,
                                    String descripcion, String rolRevisor) {
        LocalDateTime ahora = LocalDateTime.now();
        return new Observacion(null, idTramite, idRevisor, tipoObservacion,
                descripcion, ObservacionEstado.PENDIENTE, rolRevisor, ahora, ahora);
    }

    public boolean marcarSubsanada() {
        if (this.estado != ObservacionEstado.PENDIENTE) {
            return false;
        }
        this.estado = ObservacionEstado.SUBSANADA;
        this.fechaActualizacion = LocalDateTime.now();
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

    public Integer getId() { return id; }
    public Integer getIdTramite() { return idTramite; }
    public Integer getIdRevisor() { return idRevisor; }
    public TipoObservacion getTipoObservacion() { return tipoObservacion; }
    public String getDescripcion() { return descripcion; }
    public ObservacionEstado getEstado() { return estado; }
    public String getRolRevisor() { return rolRevisor; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}
