package com.sgi.fiis.resolutions.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Domain model for a Resolution.
 * Uses Java record to avoid code duplication with the infrastructure layer.
 */
public record Resolution(
        Long idResolucion,
        String numeroResolucion,
        LocalDate fechaEmision,
        String asunto,
        Long idTramite,
        Long idDocumentoAdjunto,
        LocalDateTime fechaRegistro
) {}
