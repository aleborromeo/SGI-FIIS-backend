package com.sgi.fiis.evaluaciones.domain.model;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import com.sgi.fiis.evaluaciones.domain.exception.EvaluacionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EvaluacionTest {

    @Test
    void asignarAProyectoDebeCrearEvaluacionPendiente() {
        Evaluacion evaluacion = Evaluacion.asignarAProyecto(1L, 2L);

        assertNull(evaluacion.getIdEvaluacion());
        assertEquals(1L, evaluacion.getIdProyecto());
        assertNull(evaluacion.getIdPlanTesis());
        assertEquals(2L, evaluacion.getIdEvaluador());
        assertTrue(evaluacion.estaPendiente());
        assertTrue(evaluacion.perteneceAProyecto());
        assertFalse(evaluacion.perteneceAPlanTesis());
        assertNotNull(evaluacion.getFechaAsignacion());
    }

    @Test
    void asignarAPlanTesisDebeCrearEvaluacionPendiente() {
        Evaluacion evaluacion = Evaluacion.asignarAPlanTesis(5L, 2L);

        assertNull(evaluacion.getIdEvaluacion());
        assertNull(evaluacion.getIdProyecto());
        assertEquals(5L, evaluacion.getIdPlanTesis());
        assertEquals(2L, evaluacion.getIdEvaluador());
        assertTrue(evaluacion.estaPendiente());
        assertFalse(evaluacion.perteneceAProyecto());
        assertTrue(evaluacion.perteneceAPlanTesis());
        assertNotNull(evaluacion.getFechaAsignacion());
    }

    @Test
    void registrarResultadoAprobadoDebeActualizarEvaluacion() {
        Evaluacion evaluacion = Evaluacion.asignarAProyecto(1L, 2L);

        evaluacion.registrarResultado(
                ResultadoEvaluacion.APROBADO,
                90,
                "Cumple con los criterios establecidos."
        );

        assertEquals(ResultadoEvaluacion.APROBADO, evaluacion.getResultado());
        assertEquals(90, evaluacion.getPuntaje());
        assertEquals("Cumple con los criterios establecidos.", evaluacion.getObservaciones());
        assertNotNull(evaluacion.getFechaEvaluacion());
        assertFalse(evaluacion.estaPendiente());
    }

    @Test
    void registrarResultadoRechazadoSinObservacionDebeLanzarExcepcion() {
        Evaluacion evaluacion = Evaluacion.asignarAProyecto(1L, 2L);

        EvaluacionException exception = assertThrows(
                EvaluacionException.class,
                () -> evaluacion.registrarResultado(ResultadoEvaluacion.RECHAZADO, 30, "")
        );

        assertEquals("Debe registrar observaciones cuando el resultado es RECHAZADO.", exception.getMessage());
    }

    @Test
    void registrarResultadoConObservacionesSinObservacionDebeLanzarExcepcion() {
        Evaluacion evaluacion = Evaluacion.asignarAProyecto(1L, 2L);

        EvaluacionException exception = assertThrows(
                EvaluacionException.class,
                () -> evaluacion.registrarResultado(ResultadoEvaluacion.CON_OBSERVACIONES, 60, null)
        );

        assertEquals("Debe registrar observaciones cuando el resultado es CON_OBSERVACIONES.", exception.getMessage());
    }

    @Test
    void registrarResultadoConPuntajeMenorACeroDebeLanzarExcepcion() {
        Evaluacion evaluacion = Evaluacion.asignarAProyecto(1L, 2L);

        assertThrows(
                EvaluacionException.class,
                () -> evaluacion.registrarResultado(ResultadoEvaluacion.APROBADO, -1, "Observación")
        );
    }

    @Test
    void registrarResultadoConPuntajeMayorACienDebeLanzarExcepcion() {
        Evaluacion evaluacion = Evaluacion.asignarAProyecto(1L, 2L);

        assertThrows(
                EvaluacionException.class,
                () -> evaluacion.registrarResultado(ResultadoEvaluacion.APROBADO, 101, "Observación")
        );
    }

    @Test
    void registrarResultadoNuloDebeLanzarExcepcion() {
        Evaluacion evaluacion = Evaluacion.asignarAProyecto(1L, 2L);

        assertThrows(
                EvaluacionException.class,
                () -> evaluacion.registrarResultado(null, 80, "Observación")
        );
    }

    @Test
    void reconstruirConProyectoYPlanTesisDebeLanzarExcepcion() {
        assertThrows(
                EvaluacionException.class,
                () -> Evaluacion.reconstruir(
                        1L,
                        1L,
                        1L,
                        2L,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @Test
    void reconstruirSinProyectoNiPlanTesisDebeLanzarExcepcion() {
        assertThrows(
                EvaluacionException.class,
                () -> Evaluacion.reconstruir(
                        1L,
                        null,
                        null,
                        2L,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @Test
    void reconstruirSinEvaluadorDebeLanzarExcepcion() {
        assertThrows(
                EvaluacionException.class,
                () -> Evaluacion.reconstruir(
                        1L,
                        1L,
                        null,
                        null,
                        null,
                        null,
                        null,
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @Test
    void reconstruirSinFechaAsignacionDebeLanzarExcepcion() {
        assertThrows(
                EvaluacionException.class,
                () -> Evaluacion.reconstruir(
                        1L,
                        1L,
                        null,
                        2L,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
    }
}