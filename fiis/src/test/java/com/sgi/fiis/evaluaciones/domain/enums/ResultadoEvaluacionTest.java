package com.sgi.fiis.evaluaciones.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultadoEvaluacionTest {

    @Test
    void valuesDebeRetornarTodosLosResultadosEsperados() {
        ResultadoEvaluacion[] values = ResultadoEvaluacion.values();

        assertArrayEquals(
                new ResultadoEvaluacion[]{
                        ResultadoEvaluacion.APROBADO,
                        ResultadoEvaluacion.RECHAZADO,
                        ResultadoEvaluacion.CON_OBSERVACIONES
                },
                values
        );
    }

    @Test
    void valueOfDebeRetornarAprobado() {
        ResultadoEvaluacion resultado = ResultadoEvaluacion.valueOf("APROBADO");

        assertEquals(ResultadoEvaluacion.APROBADO, resultado);
    }

    @Test
    void valueOfDebeRetornarRechazado() {
        ResultadoEvaluacion resultado = ResultadoEvaluacion.valueOf("RECHAZADO");

        assertEquals(ResultadoEvaluacion.RECHAZADO, resultado);
    }

    @Test
    void valueOfDebeRetornarConObservaciones() {
        ResultadoEvaluacion resultado = ResultadoEvaluacion.valueOf("CON_OBSERVACIONES");

        assertEquals(ResultadoEvaluacion.CON_OBSERVACIONES, resultado);
    }
}