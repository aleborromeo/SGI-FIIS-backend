package com.sgi.fiis.evaluaciones.application.dto.response;

public record EvaluadorDisponibleResponse(
        Long id,
        String firstNames,
        String lastNames,
        String institutionalEmail,
        String roleCode,
        String roleDescription
) {
}
