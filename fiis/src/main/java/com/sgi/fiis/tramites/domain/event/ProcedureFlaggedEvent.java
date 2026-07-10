package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.ProcedureType;
import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcedureFlaggedEvent {

    private final Long procedureId;
    private final String code;
    private final ProcedureType procedureType;
    private final Long applicantId;
    private final Long observerId;
    private final RoleEnum observerRole;
    private final String observationText;
    private final LocalDateTime observationDate;

    public Long getIdTramite() {
        return procedureId;
    }

    public Long getIdObservador() {
        return observerId;
    }

    public String getTextoObservacion() {
        return observationText;
    }

    public LocalDateTime getFechaObservacion() {
        return observationDate;
    }
}
