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

    private final Long idTramite;
    private final String codigoTramite;
    private final ProcedureType tipoTramite;
    private final Long idSolicitante;
    private final ProcedureStatus estadoResultante;
    private final Long idAprobador;
    private final RoleEnum rolAprobador;
    private final LocalDateTime fechaAprobacion;
}
