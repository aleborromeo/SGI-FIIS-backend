package com.sgi.fiis.thesis.domain;

import java.time.LocalDateTime;
import com.sgi.fiis.thesis.domain.exception.InvalidStateTransitionException;

public class ThesisPlan {
    private Integer idPlanTesis;
    private String tituloTesis;
    private String resumen;
    private Long idEstudiante;
    private Integer idLinea;
    private Integer idGrupo;
    private Integer idDocumentoActual;
    private ThesisPlanStatus estadoPlan;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    @SuppressWarnings("java:S107")
    public ThesisPlan(Integer idPlanTesis, String tituloTesis, String resumen, Long idEstudiante,
                     Integer idLinea, Integer idGrupo, Integer idDocumentoActual, ThesisPlanStatus estadoPlan,
                     LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.idPlanTesis = idPlanTesis;
        this.tituloTesis = tituloTesis;
        this.resumen = resumen;
        this.idEstudiante = idEstudiante;
        this.idLinea = idLinea;
        this.idGrupo = idGrupo;
        this.idDocumentoActual = idDocumentoActual;
        this.estadoPlan = estadoPlan == null ? ThesisPlanStatus.POSTULADO : estadoPlan;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public static ThesisPlan nuevo(String tituloTesis, String resumen, Long idEstudiante,
                                  Integer idLinea, Integer idGrupo, Integer idDocumentoActual) {
        return new ThesisPlan(null, tituloTesis, resumen, idEstudiante, idLinea, idGrupo,
                idDocumentoActual, ThesisPlanStatus.POSTULADO, null, null);
    }

    public void marcarObservado() {
        if (estadoPlan == ThesisPlanStatus.RECHAZADO) {
            throw new InvalidStateTransitionException("No se puede observar un plan en estado " + estadoPlan);
        }
        estadoPlan = ThesisPlanStatus.OBSERVADO;
    }

    public void marcarAprobado() {
        if (estadoPlan == ThesisPlanStatus.RECHAZADO) {
            throw new InvalidStateTransitionException("No se puede aprobar un plan rechazado");
        }
        estadoPlan = ThesisPlanStatus.APROBADO;
    }

    public void marcarRechazado() {
        if (estadoPlan == ThesisPlanStatus.APROBADO) {
            throw new InvalidStateTransitionException("No se puede rechazar un plan aprobado");
        }
        estadoPlan = ThesisPlanStatus.RECHAZADO;
    }

    public void subsanar(Integer nuevoDocumentoId, String nuevoResumen) {
        if (estadoPlan != ThesisPlanStatus.OBSERVADO) {
            throw new InvalidStateTransitionException("Solo se puede subsanar un plan observado");
        }
        if (nuevoDocumentoId != null) this.idDocumentoActual = nuevoDocumentoId;
        if (nuevoResumen != null && !nuevoResumen.isBlank()) this.resumen = nuevoResumen;
        estadoPlan = ThesisPlanStatus.POSTULADO;
    }

    public Integer getIdPlanTesis() { return idPlanTesis; }
    public String getTituloTesis() { return tituloTesis; }
    public String getResumen() { return resumen; }
    public Long getIdEstudiante() { return idEstudiante; }
    public Integer getIdLinea() { return idLinea; }
    public Integer getIdGrupo() { return idGrupo; }
    public Integer getIdDocumentoActual() { return idDocumentoActual; }
    public ThesisPlanStatus getEstadoPlan() { return estadoPlan; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}
