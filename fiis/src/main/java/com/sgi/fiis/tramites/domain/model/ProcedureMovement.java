package com.sgi.fiis.tramites.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcedureMovement {

    private final Long actionUserId;
    private final String action;
    private final ProcedureStatus previousStatus;
    private final ProcedureStatus newStatus;
    private final String comment;
    private final LocalDateTime movementAt;
}
