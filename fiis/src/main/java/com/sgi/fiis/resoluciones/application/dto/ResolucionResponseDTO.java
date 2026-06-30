package com.sgi.fiis.resoluciones.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResolucionResponseDTO(
    Long idResolucion,
    String numeroResolucion,
    LocalDate fechaEmision,
    String asunto,
    Long idTramite,
    Long idDocumentoAdjunto,
    LocalDateTime fechaRegistro
) {}
