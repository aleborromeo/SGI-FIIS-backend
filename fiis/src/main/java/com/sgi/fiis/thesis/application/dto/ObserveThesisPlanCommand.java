package com.sgi.fiis.thesis.application.dto;

import jakarta.validation.constraints.NotBlank;

public record ObserveThesisPlanCommand(
        @NotBlank String observacion,
        Integer idDocumentoAdjunto
) {}
