package com.sgi.fiis.observaciones.application.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CorrectionRequestDto {
    private Integer requesterId;
    private String description;
    private Integer attachedDocumentId;
}
