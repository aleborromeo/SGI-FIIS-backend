package com.sgi.fiis.observations.application.dto;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObservationRequestDTO {
    private Integer procedureId;
    private Integer reviewerId;
    
    @Schema(example = "TECNICA", description = "Valores permitidos: TECNICA, DOCUMENTAL, PRESUPUESTAL, FORMATO")
    private String type;
    
    private String description;
    private String reviewerRole;
}
