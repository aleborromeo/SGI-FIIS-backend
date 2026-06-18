package com.sgi.fiis.observaciones.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CorrectionResponseDto {
    private Integer id;
    private Integer observationId;
    private Integer requesterId;
    private String description;
    private Integer attachedDocumentId;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
}
