package com.sgi.fiis.observations.application.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RemedyRequestDTO {
    private Integer applicantId;
    private String description;
    private Integer attachedDocumentId;
}
