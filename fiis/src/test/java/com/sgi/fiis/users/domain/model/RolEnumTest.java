package com.sgi.fiis.users.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RolEnum Unit Tests")
class RolEnumTest {

    @Test
    @DisplayName("Should return correct description for each role")
    void testGetDescripcion() {
        assertEquals("Administrador del sistema", RolEnum.ADMIN.getDescripcion());
        assertEquals("Estudiante / Tesista", RolEnum.ESTUDIANTE.getDescripcion());
        assertEquals("Docente Investigador", RolEnum.DOCENTE_INVESTIGADOR.getDescripcion());
        assertEquals("Coordinador de Grupo de Investigación", RolEnum.COORDINADOR_GRUPO.getDescripcion());
        assertEquals("Director de Investigación", RolEnum.DIRECTOR_INVESTIGACION.getDescripcion());
        assertEquals("Decano de la Facultad", RolEnum.DECANO.getDescripcion());
        assertEquals("Evaluador de proyectos", RolEnum.EVALUADOR.getDescripcion());
    }

    @Test
    @DisplayName("Should check values and valueOf methods")
    void testEnumMethods() {
        RolEnum[] values = RolEnum.values();
        assertEquals(7, values.length);
        assertEquals(RolEnum.ADMIN, RolEnum.valueOf("ADMIN"));
    }
}
