package com.sgi.fiis.observaciones.application.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObservationRequestDto {
    private Integer procedureId;
    private Integer reviewerId;
    private String observationType;
    private String description;
    private String reviewerRole;
}
