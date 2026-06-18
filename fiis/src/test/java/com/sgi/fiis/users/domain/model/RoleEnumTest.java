package com.sgi.fiis.users.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoleEnum Unit Tests")
class RoleEnumTest {

    @Test
    @DisplayName("Should return correct description for each role")
    void testGetDescripcion() {
        assertEquals("Administrador del sistema", RoleEnum.ADMIN.getDescription());
        assertEquals("Estudiante / Tesista", RoleEnum.ESTUDIANTE.getDescription());
        assertEquals("Docente Investigador", RoleEnum.DOCENTE_INVESTIGADOR.getDescription());
        assertEquals("Coordinador de Grupo de Investigación", RoleEnum.COORDINADOR_GRUPO.getDescription());
        assertEquals("Director de Investigación", RoleEnum.DIRECTOR_INVESTIGACION.getDescription());
        assertEquals("Decano de la Facultad", RoleEnum.DECANO.getDescription());
        assertEquals("Evaluador de proyectos", RoleEnum.EVALUADOR.getDescription());
    }

    @Test
    @DisplayName("Should check values and valueOf methods")
    void testEnumMethods() {
        RoleEnum[] values = RoleEnum.values();
        assertEquals(7, values.length);
        assertEquals(RoleEnum.ADMIN, RoleEnum.valueOf("ADMIN"));
    }
}
