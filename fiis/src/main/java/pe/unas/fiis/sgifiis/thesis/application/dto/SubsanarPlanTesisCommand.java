package pe.unas.fiis.sgifiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;

public record SubsanarPlanTesisCommand(
        Integer idDocumentoActual,
        String resumenSubsanado,
        @NotBlank(message = "Debe indicar qué observaciones corrigió")
        String comentarioSubsanacion
) {}
