package com.sgi.fiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegisterResolutionCommand(
        @NotBlank String numeroResolucion,
        @NotNull LocalDate fechaEmision,
        @NotBlank String asunto,
        @NotNull Integer idDocumentoAdjunto
) {}
