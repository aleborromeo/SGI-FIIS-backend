package pe.unas.fiis.sgifiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ObservarPlanTesisCommand(
        @NotNull Integer idUsuarioAccion,
        @NotBlank String observacion,
        Integer idDocumentoAdjunto
) {}
