package com.sgi.fiis.tramites.application.dto;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureMovementResponseDto {

    private Long actionUserId;
    private String action;
    private ProcedureStatus previousStatus;
    private ProcedureStatus newStatus;
    private String comment;
    private LocalDateTime movementAt;
}
