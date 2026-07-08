package com.sgi.fiis.resolutions.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResolutionResponseDTO(
    Long idResolucion,
    String numeroResolucion,
    LocalDate fechaEmision,
    String asunto,
    Long idTramite,
    Long idDocumentoAdjunto,
    LocalDateTime fechaRegistro
) {}
