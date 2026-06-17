package com.sgi.fiis.documentacion.application.dto;

import java.time.LocalDateTime;

public record DocumentResponseDto(
    Long id,
    String originalName,
    String extension,
    Long sizeBytes,
    Long uploadedById,
    LocalDateTime uploadDate,
    Long proyectoId,
    Long tramiteId,
    Long planTesisId,
    Long informeId,
    boolean esSubsanacion
) {}