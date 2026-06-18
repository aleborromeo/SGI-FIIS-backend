package com.sgi.fiis.observations.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RemedyResponseDTO {
    private Integer id;
    private Integer observationId;
    private Integer applicantId;
    private String description;
    private Integer attachedDocumentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
