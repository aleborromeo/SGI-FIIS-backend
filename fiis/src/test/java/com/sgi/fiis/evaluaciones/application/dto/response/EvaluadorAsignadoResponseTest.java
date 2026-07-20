package com.sgi.fiis.evaluaciones.application.dto.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvaluadorAsignadoResponseTest {

    @Test
    void testRecordGettersAndConstructors() {
        EvaluadorAsignadoResponse response = new EvaluadorAsignadoResponse(
                1L,
                "Juan",
                "Perez",
                "juan.perez@sgi.com",
                "EVALUADOR",
                "APROBADO",
                true
        );

        assertEquals(1L, response.id());
        assertEquals("Juan", response.nombres());
        assertEquals("Perez", response.apellidos());
        assertEquals("juan.perez@sgi.com", response.correoInstitucional());
        assertEquals("EVALUADOR", response.codigoRol());
        assertEquals("APROBADO", response.resultado());
        assertTrue(response.pendiente());
    }
}
