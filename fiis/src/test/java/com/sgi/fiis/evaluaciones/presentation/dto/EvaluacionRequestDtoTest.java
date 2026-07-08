package com.sgi.fiis.evaluaciones.presentation.dto;

import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluacionRequestDtoTest {

    @Test
    void asignarEvaluadorRequestDebeGuardarValores() {
        AsignarEvaluadorRequest request = new AsignarEvaluadorRequest(1L, null, 2L);

        assertEquals(1L, request.idProyecto());
        assertNull(request.idPlanTesis());
        assertEquals(2L, request.idEvaluador());
    }

    @Test
    void registrarResultadoEvaluacionRequestDebeGuardarValores() {
        RegistrarResultadoEvaluacionRequest request = new RegistrarResultadoEvaluacionRequest(
                2L,
                ResultadoEvaluacion.APROBADO,
                90,
                "Cumple con los criterios."
        );

        assertEquals(2L, request.idEvaluador());
        assertEquals(ResultadoEvaluacion.APROBADO, request.resultado());
        assertEquals(90, request.puntaje());
        assertEquals("Cumple con los criterios.", request.observaciones());
    }
}