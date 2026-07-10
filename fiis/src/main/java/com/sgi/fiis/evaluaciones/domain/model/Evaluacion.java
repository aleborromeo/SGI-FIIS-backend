package com.sgi.fiis.evaluaciones.domain.model;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import com.sgi.fiis.evaluaciones.domain.exception.EvaluacionException;

import java.time.LocalDateTime;

public class Evaluacion {

    private final Long idEvaluacion;
    private final Long idProyecto;
    private final Long idPlanTesis;
    private final Long idEvaluador;

    private ResultadoEvaluacion resultado;
    private Integer puntaje;
    private String observaciones;

    private final LocalDateTime fechaAsignacion;
    private LocalDateTime fechaEvaluacion;

    private Evaluacion(Builder builder) {
        this.idEvaluacion = builder.idEvaluacion;
        this.idProyecto = builder.idProyecto;
        this.idPlanTesis = builder.idPlanTesis;
        this.idEvaluador = builder.idEvaluador;
        this.resultado = builder.resultado;
        this.puntaje = builder.puntaje;
        this.observaciones = builder.observaciones;
        this.fechaAsignacion = builder.fechaAsignacion;
        this.fechaEvaluacion = builder.fechaEvaluacion;

        validarAsignacion();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long idEvaluacion;
        private Long idProyecto;
        private Long idPlanTesis;
        private Long idEvaluador;
        private ResultadoEvaluacion resultado;
        private Integer puntaje;
        private String observaciones;
        private LocalDateTime fechaAsignacion;
        private LocalDateTime fechaEvaluacion;

        private Builder() {}

        public Builder idEvaluacion(Long v)           { this.idEvaluacion = v; return this; }
        public Builder idProyecto(Long v)             { this.idProyecto = v; return this; }
        public Builder idPlanTesis(Long v)            { this.idPlanTesis = v; return this; }
        public Builder idEvaluador(Long v)            { this.idEvaluador = v; return this; }
        public Builder resultado(ResultadoEvaluacion v){ this.resultado = v; return this; }
        public Builder puntaje(Integer v)             { this.puntaje = v; return this; }
        public Builder observaciones(String v)        { this.observaciones = v; return this; }
        public Builder fechaAsignacion(LocalDateTime v){ this.fechaAsignacion = v; return this; }
        public Builder fechaEvaluacion(LocalDateTime v){ this.fechaEvaluacion = v; return this; }

        public Evaluacion build() { return new Evaluacion(this); }
    }

    public static Evaluacion asignarAProyecto(Long idProyecto, Long idEvaluador) {
        return Evaluacion.builder()
                .idProyecto(idProyecto)
                .idEvaluador(idEvaluador)
                .fechaAsignacion(LocalDateTime.now(java.time.ZoneId.systemDefault()))
                .build();
    }

    public static Evaluacion asignarAPlanTesis(Long idPlanTesis, Long idEvaluador) {
        return Evaluacion.builder()
                .idPlanTesis(idPlanTesis)
                .idEvaluador(idEvaluador)
                .fechaAsignacion(LocalDateTime.now(java.time.ZoneId.systemDefault()))
                .build();
    }

    @SuppressWarnings("java:S107")
    public static Evaluacion reconstruir(
            Long idEvaluacion,
            Long idProyecto,
            Long idPlanTesis,
            Long idEvaluador,
            ResultadoEvaluacion resultado,
            Integer puntaje,
            String observaciones,
            LocalDateTime fechaAsignacion,
            LocalDateTime fechaEvaluacion
    ) {
        return Evaluacion.builder()
                .idEvaluacion(idEvaluacion)
                .idProyecto(idProyecto)
                .idPlanTesis(idPlanTesis)
                .idEvaluador(idEvaluador)
                .resultado(resultado)
                .puntaje(puntaje)
                .observaciones(observaciones)
                .fechaAsignacion(fechaAsignacion)
                .fechaEvaluacion(fechaEvaluacion)
                .build();
    }

    public void registrarResultado(
            ResultadoEvaluacion resultado,
            Integer puntaje,
            String observaciones
    ) {
        if (resultado == null) {
            throw new EvaluacionException("El resultado de la evaluación es obligatorio.");
        }

        if (puntaje != null && (puntaje < 0 || puntaje > 100)) {
            throw new EvaluacionException("El puntaje debe estar entre 0 y 100.");
        }

        if (resultado == ResultadoEvaluacion.CON_OBSERVACIONES && estaVacio(observaciones)) {
            throw new EvaluacionException("Debe registrar observaciones cuando el resultado es CON_OBSERVACIONES.");
        }

        if (resultado == ResultadoEvaluacion.RECHAZADO && estaVacio(observaciones)) {
            throw new EvaluacionException("Debe registrar observaciones cuando el resultado es RECHAZADO.");
        }

        this.resultado = resultado;
        this.puntaje = puntaje;
        this.observaciones = observaciones;
        this.fechaEvaluacion = LocalDateTime.now(java.time.ZoneId.systemDefault());
    }

    public boolean estaPendiente() {
        return resultado == null && fechaEvaluacion == null;
    }

    public boolean perteneceAProyecto() {
        return idProyecto != null;
    }

    public boolean perteneceAPlanTesis() {
        return idPlanTesis != null;
    }

    private void validarAsignacion() {
        if (idEvaluador == null) {
            throw new EvaluacionException("El evaluador es obligatorio.");
        }

        boolean tieneProyecto = idProyecto != null;
        boolean tienePlanTesis = idPlanTesis != null;

        if (tieneProyecto && tienePlanTesis) {
            throw new EvaluacionException("La evaluación no puede pertenecer a un proyecto y a un plan de tesis al mismo tiempo.");
        }

        if (!tieneProyecto && !tienePlanTesis) {
            throw new EvaluacionException("La evaluación debe estar asociada a un proyecto o a un plan de tesis.");
        }

        if (fechaAsignacion == null) {
            throw new EvaluacionException("La fecha de asignación es obligatoria.");
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public Long getIdEvaluacion() {
        return idEvaluacion;
    }

    public Long getIdProyecto() {
        return idProyecto;
    }

    public Long getIdPlanTesis() {
        return idPlanTesis;
    }

    public Long getIdEvaluador() {
        return idEvaluador;
    }

    public ResultadoEvaluacion getResultado() {
        return resultado;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }
}