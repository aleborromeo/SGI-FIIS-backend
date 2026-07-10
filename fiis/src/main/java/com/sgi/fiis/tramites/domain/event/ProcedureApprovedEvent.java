package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.ProcedureStatus;
import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcedureApprovedEvent {

    private final Long procedureId;
    private final String code;
    private final ProcedureType procedureType;
    private final Long applicantId;
    private final ProcedureStatus resultingStatus;
    private final Long approverId;
    private final RoleEnum approverRole;
    private final LocalDateTime approvalDate;

    public Long getIdTramite() {
        return procedureId;
    }

    public Long getIdAprobador() {
        return approverId;
    }

    public LocalDateTime getFechaAprobacion() {
        return approvalDate;
    }
}
