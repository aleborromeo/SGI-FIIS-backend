package com.sgi.fiis.thesis.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterThesisPlanCommand(
        @NotBlank @Size(max = 500) String tituloTesis,
        @NotBlank @Size(max = 5000) String resumen,
        @NotNull @Min(1) Integer idLinea,
        @NotNull @Min(1) Integer idGrupo,
        @NotNull @Min(1) Integer idDocumentoActual
) {}
