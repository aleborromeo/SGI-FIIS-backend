package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.ProcedureType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcedureFinalizedEvent {

    private final Long procedureId;
    private final String code;
    private final ProcedureType procedureType;
    private final Long applicantId;
    private final LocalDateTime finalizationDate;

    public Long getIdTramite() {
        return procedureId;
    }

    public LocalDateTime getFechaFinalizacion() {
        return finalizationDate;
    }
}
