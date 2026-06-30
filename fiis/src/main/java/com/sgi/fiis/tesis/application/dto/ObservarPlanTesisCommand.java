package pe.unas.fiis.sgifiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ObservarPlanTesisCommand(
        @NotBlank String observacion,
        Integer idDocumentoAdjunto
) {}
