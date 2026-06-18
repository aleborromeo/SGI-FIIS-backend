package com.sgi.fiis.observaciones.application.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ObservationResponseDto {
    private Integer id;
    private Integer procedureId;
    private Integer reviewerId;
    private String observationType;
    private String description;
    private String status;
    private String reviewerRole;
    private LocalDateTime registeredAt;
    private LocalDateTime updatedAt;
}
