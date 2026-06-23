package com.sgi.fiis.observations.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObservationResponseDTO {
    private Integer id;
    private Integer procedureId;
    private Integer reviewerId;
    private String type;
    private String description;
    private String status;
    private String reviewerRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
