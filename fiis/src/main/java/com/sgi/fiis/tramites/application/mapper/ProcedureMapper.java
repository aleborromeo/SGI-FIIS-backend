package com.sgi.fiis.tramites.application.mapper;

import com.sgi.fiis.tramites.application.dto.ProcedureMovementResponseDto;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.domain.model.ProcedureMovement;
import com.sgi.fiis.tramites.domain.model.Procedure;

import java.util.List;

public class ProcedureMapper {

    private ProcedureMapper() {}

    public static ProcedureResponseDto toResponse(Procedure tramite) {
        return ProcedureResponseDto.builder()
                .id(tramite.getId())
                .code(tramite.getCode())
                .procedureType(tramite.getProcedureType())
                .currentStatus(tramite.getCurrentStatus())
                .applicantId(tramite.getApplicantId())
                .groupId(tramite.getGroupId())
                .currentReviewerRole(tramite.getCurrentReviewerRole())
                .currentObservation(tramite.getCurrentObservation())
                .projectReferenceId(tramite.getProjectReferenceId())
                .thesisReferenceId(tramite.getThesisReferenceId())
                .reportReferenceId(tramite.getReportReferenceId())
                .sentAt(tramite.getSentAt())
                .updatedAt(tramite.getUpdatedAt())
                .build();
    }

    public static ProcedureMovementResponseDto toMovementResponse(ProcedureMovement movimiento) {
        return ProcedureMovementResponseDto.builder()
                .actionUserId(movimiento.getActionUserId())
                .action(movimiento.getAction())
                .previousStatus(movimiento.getPreviousStatus())
                .newStatus(movimiento.getNewStatus())
                .comment(movimiento.getComment())
                .movementAt(movimiento.getMovementAt())
                .build();
    }

    public static List<ProcedureMovementResponseDto> toMovementResponseList(List<ProcedureMovement> movimientos) {
        return movimientos.stream()
                .map(ProcedureMapper::toMovementResponse)
                .toList();
    }
}
