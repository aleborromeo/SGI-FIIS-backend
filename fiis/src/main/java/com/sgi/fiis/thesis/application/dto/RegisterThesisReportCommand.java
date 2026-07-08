package com.sgi.fiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterThesisReportCommand(
        @NotNull Integer idPlanTesis,
        @NotBlank @Size(max = 500) String tituloFinal,
        @NotNull Integer idDocumentoTesis
) {}
