package com.sgi.fiis.users.domain.model;

/**
 * Enum with the system roles.
 * Each value corresponds to the role code in the database.
 */
public enum RoleEnum {
    ADMIN("Administrador del sistema"),
    ESTUDIANTE("Estudiante / Tesista"),
    DOCENTE_INVESTIGADOR("Docente Investigador"),
    COORDINADOR_GRUPO("Coordinador de Grupo de Investigación"),
    DIRECTOR_INVESTIGACION("Director de Investigación"),
    DECANO("Decano de la Facultad"),
    EVALUADOR("Evaluador de proyectos");

    private final String description;

    RoleEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
