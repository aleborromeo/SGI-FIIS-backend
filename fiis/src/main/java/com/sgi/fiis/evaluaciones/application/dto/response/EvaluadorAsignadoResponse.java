package com.sgi.fiis.evaluaciones.application.dto.response;

public record EvaluadorAsignadoResponse(
        Long id,
        String nombres,
        String apellidos,
        String correoInstitucional,
        String codigoRol,
        String resultado,
        boolean pendiente
) {}
