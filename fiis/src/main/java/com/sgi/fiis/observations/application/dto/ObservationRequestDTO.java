package com.sgi.fiis.observations.application.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObservationRequestDTO {
    private Integer procedureId;
    private Integer reviewerId;
    private String type;
    private String description;
    private String reviewerRole;
}
