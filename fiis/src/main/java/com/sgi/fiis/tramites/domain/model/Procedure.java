package com.sgi.fiis.tramites.domain.model;

import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class Procedure {

    private Long id;
    private String code;
    private ProcedureType procedureType;
    private Long applicantId;
    private Long groupId;
    private ProcedureStatus currentStatus;
    private RoleEnum currentReviewerRole;
    private String currentObservation;

    private Long projectReferenceId;
    private Long thesisReferenceId;
    private Long reportReferenceId;

    private LocalDateTime sentAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<ProcedureMovement> movements = new ArrayList<>();

    public List<ProcedureMovement> getMovements() {
        return Collections.unmodifiableList(movements);
    }

    public void transitionTo(
            ProcedureStatus newStatus,
            RoleEnum executingRole,
            Long actionUserId,
            String action,
            String comment,
            RoleEnum newReviewerRole) {

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new InvalidTransitionException(currentStatus, newStatus, executingRole);
        }

        ProcedureStatus previousStatus = this.currentStatus;
        this.currentStatus     = newStatus;
        this.currentReviewerRole = newReviewerRole;
        this.currentObservation = comment;
        this.updatedAt = LocalDateTime.now(ZoneId.systemDefault());

        movements.add(ProcedureMovement.builder()
                .actionUserId(actionUserId)
                .action(action)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .comment(comment)
                .movementAt(this.updatedAt)
                .build());
    }

    public void validateExclusiveReference() {
        int count = (projectReferenceId != null ? 1 : 0)
                  + (thesisReferenceId    != null ? 1 : 0)
                  + (reportReferenceId    != null ? 1 : 0);
        if (count != 1) {
            throw new IllegalArgumentException(
                    "El trámite debe referenciar exactamente una entidad origen " +
                    "(proyecto, tesis o informe). Referencias encontradas: " + count
            );
        }
    }
}
