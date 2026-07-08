package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.ProcedureType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProcedureFinalizedEvent {

    private final Long idTramite;
    private final String codigoTramite;
    private final ProcedureType tipoTramite;
    private final Long idSolicitante;
    private final LocalDateTime fechaFinalizacion;
}
