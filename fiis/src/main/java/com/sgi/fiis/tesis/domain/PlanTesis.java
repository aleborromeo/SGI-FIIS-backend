package pe.unas.fiis.sgifiis.thesis.domain;

import java.time.LocalDateTime;
import pe.unas.fiis.sgifiis.thesis.domain.exception.TransicionEstadoInvalidaException;

public class PlanTesis {
    private Integer idPlanTesis;
    private String tituloTesis;
    private String resumen;
    private Integer idEstudiante;
    private Integer idLinea;
    private Integer idGrupo;
    private Integer idDocumentoActual;
    private EstadoPlanTesis estadoPlan;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public PlanTesis(Integer idPlanTesis, String tituloTesis, String resumen, Integer idEstudiante,
                     Integer idLinea, Integer idGrupo, Integer idDocumentoActual, EstadoPlanTesis estadoPlan,
                     LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.idPlanTesis = idPlanTesis;
        this.tituloTesis = tituloTesis;
        this.resumen = resumen;
        this.idEstudiante = idEstudiante;
        this.idLinea = idLinea;
        this.idGrupo = idGrupo;
        this.idDocumentoActual = idDocumentoActual;
        this.estadoPlan = estadoPlan == null ? EstadoPlanTesis.POSTULADO : estadoPlan;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public static PlanTesis nuevo(String tituloTesis, String resumen, Integer idEstudiante,
                                  Integer idLinea, Integer idGrupo, Integer idDocumentoActual) {
        return new PlanTesis(null, tituloTesis, resumen, idEstudiante, idLinea, idGrupo,
                idDocumentoActual, EstadoPlanTesis.POSTULADO, null, null);
    }

    public void marcarObservado() {
        if (estadoPlan == EstadoPlanTesis.APROBADO || estadoPlan == EstadoPlanTesis.RECHAZADO) {
            throw new TransicionEstadoInvalidaException("No se puede observar un plan en estado " + estadoPlan);
        }
        estadoPlan = EstadoPlanTesis.OBSERVADO;
    }

    public void marcarAprobado() {
        if (estadoPlan == EstadoPlanTesis.RECHAZADO) {
            throw new TransicionEstadoInvalidaException("No se puede aprobar un plan rechazado");
        }
        estadoPlan = EstadoPlanTesis.APROBADO;
    }

    public void marcarRechazado() {
        if (estadoPlan == EstadoPlanTesis.APROBADO) {
            throw new TransicionEstadoInvalidaException("No se puede rechazar un plan aprobado");
        }
        estadoPlan = EstadoPlanTesis.RECHAZADO;
    }

    public void subsanar(Integer nuevoDocumentoId, String nuevoResumen) {
        if (estadoPlan != EstadoPlanTesis.OBSERVADO) {
            throw new TransicionEstadoInvalidaException("Solo se puede subsanar un plan observado");
        }
        if (nuevoDocumentoId != null) this.idDocumentoActual = nuevoDocumentoId;
        if (nuevoResumen != null && !nuevoResumen.isBlank()) this.resumen = nuevoResumen;
        estadoPlan = EstadoPlanTesis.POSTULADO;
    }

    public Integer getIdPlanTesis() { return idPlanTesis; }
    public String getTituloTesis() { return tituloTesis; }
    public String getResumen() { return resumen; }
    public Integer getIdEstudiante() { return idEstudiante; }
    public Integer getIdLinea() { return idLinea; }
    public Integer getIdGrupo() { return idGrupo; }
    public Integer getIdDocumentoActual() { return idDocumentoActual; }
    public EstadoPlanTesis getEstadoPlan() { return estadoPlan; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
}
