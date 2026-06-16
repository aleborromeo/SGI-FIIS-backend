package com.sgi.fiis.documentacion.application.dto;

import java.time.LocalDateTime;

public record DocumentResponseDto(
    Long id,
    String originalName,
    String extension,
    Long sizeBytes,
    Integer uploadedById,
    LocalDateTime uploadDate
) {}