package com.sgi.fiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;

public record RectifyThesisPlanCommand(
        Integer idDocumentoActual,
        String resumenSubsanado,
        @NotBlank(message = "Debe indicar qué observaciones corrigió")
        String comentarioSubsanacion
) {}
