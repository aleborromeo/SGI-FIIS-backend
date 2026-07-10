package com.sgi.fiis.tramites.application.dto;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedureResponseDto {

    private Long id;
    private String code;
    private ProcedureType procedureType;
    private ProcedureStatus currentStatus;
    private Long applicantId;
    private Long groupId;
    private RoleEnum currentReviewerRole;
    private String currentObservation;
    private Long projectReferenceId;
    private Long thesisReferenceId;
    private Long reportReferenceId;
    private LocalDateTime sentAt;
    private LocalDateTime updatedAt;
}
