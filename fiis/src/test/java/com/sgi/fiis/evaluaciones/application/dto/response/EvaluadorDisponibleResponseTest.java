package com.sgi.fiis.evaluaciones.application.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluadorDisponibleResponseTest {

    @Test
    void testRecordGettersAndConstructors() {
        EvaluadorDisponibleResponse response = new EvaluadorDisponibleResponse(
                1L,
                "Juan",
                "Perez",
                "juan.perez@sgi.com",
                "EVALUADOR",
                "Rol de Evaluador"
        );

        assertEquals(1L, response.id());
        assertEquals("Juan", response.firstNames());
        assertEquals("Perez", response.lastNames());
        assertEquals("juan.perez@sgi.com", response.institutionalEmail());
        assertEquals("EVALUADOR", response.roleCode());
        assertEquals("Rol de Evaluador", response.roleDescription());
    }
}
