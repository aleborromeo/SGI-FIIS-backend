package com.sgi.fiis.evaluaciones.domain.model;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import com.sgi.fiis.evaluaciones.domain.exception.EvaluacionException;

import java.time.LocalDateTime;
import java.time.ZoneId;

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

    @SuppressWarnings("java:S107")
    private Evaluacion(
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
        this.idEvaluacion = idEvaluacion;
        this.idProyecto = idProyecto;
        this.idPlanTesis = idPlanTesis;
        this.idEvaluador = idEvaluador;
        this.resultado = resultado;
        this.puntaje = puntaje;
        this.observaciones = observaciones;
        this.fechaAsignacion = fechaAsignacion;
        this.fechaEvaluacion = fechaEvaluacion;

        validarAsignacion();
    }

    public static Evaluacion asignarAProyecto(Long idProyecto, Long idEvaluador) {
        return new Evaluacion(
                null,
                idProyecto,
                null,
                idEvaluador,
                null,
                null,
                null,
                LocalDateTime.now(ZoneId.of("UTC")),
                null
        );
    }

    public static Evaluacion asignarAPlanTesis(Long idPlanTesis, Long idEvaluador) {
        return new Evaluacion(
                null,
                null,
                idPlanTesis,
                idEvaluador,
                null,
                null,
                null,
                LocalDateTime.now(ZoneId.of("UTC")),
                null
        );
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
        return new Evaluacion(
                idEvaluacion,
                idProyecto,
                idPlanTesis,
                idEvaluador,
                resultado,
                puntaje,
                observaciones,
                fechaAsignacion,
                fechaEvaluacion
        );
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
        this.fechaEvaluacion = LocalDateTime.now(ZoneId.of("UTC"));
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