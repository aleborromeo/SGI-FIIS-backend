package com.sgi.fiis.evaluaciones.infrastructure.persistence.entity;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "evaluaciones")
public class EvaluacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @Column(name = "id_proyecto")
    private Long idProyecto;

    @Column(name = "id_plan_tesis")
    private Long idPlanTesis;

    @Column(name = "id_evaluador", nullable = false)
    private Long idEvaluador;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", length = 30)
    private ResultadoEvaluacion resultado;

    @Column(name = "puntaje")
    private Integer puntaje;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;

    @Column(name = "fecha_evaluacion")
    private LocalDateTime fechaEvaluacion;

    public EvaluacionJpaEntity() {
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public void setIdEvaluacion(Long idEvaluacion) {
        this.idEvaluacion = idEvaluacion;
    }

    public Long getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Long idProyecto) {
        this.idProyecto = idProyecto;
    }

    public Long getIdPlanTesis() {
        return idPlanTesis;
    }

    public void setIdPlanTesis(Long idPlanTesis) {
        this.idPlanTesis = idPlanTesis;
    }

    public Long getIdEvaluador() {
        return idEvaluador;
    }

    public void setIdEvaluador(Long idEvaluador) {
        this.idEvaluador = idEvaluador;
    }

    public ResultadoEvaluacion getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoEvaluacion resultado) {
        this.resultado = resultado;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }
}