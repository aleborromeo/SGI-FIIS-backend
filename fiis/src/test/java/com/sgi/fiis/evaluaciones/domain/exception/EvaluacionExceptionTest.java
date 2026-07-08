package com.sgi.fiis.evaluaciones.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluacionExceptionTest {

    @Test
    void constructorDebeConservarMensaje() {
        EvaluacionException exception = new EvaluacionException("Error de evaluación");

        assertEquals("Error de evaluación", exception.getMessage());
    }

    @Test
    void debeSerRuntimeException() {
        EvaluacionException exception = new EvaluacionException("Error de dominio");

        assertInstanceOf(RuntimeException.class, exception);
    }
}