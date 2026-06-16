package com.sgi.fiis.users.domain.model;

/**
 * Enum con los roles del sistema (RF-09).
 * Cada valor corresponde al codigo_rol en la tabla 'roles'.
 */
public enum RolEnum {
    ADMIN("Administrador del sistema"),
    ESTUDIANTE("Estudiante / Tesista"),
    DOCENTE_INVESTIGADOR("Docente Investigador"),
    COORDINADOR_GRUPO("Coordinador de Grupo de Investigación"),
    DIRECTOR_INVESTIGACION("Director de Investigación"),
    DECANO("Decano de la Facultad"),
    EVALUADOR("Evaluador de proyectos");

    private final String descripcion;

    RolEnum(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
