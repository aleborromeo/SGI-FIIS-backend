package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.EstadoTramite;
import com.sgi.fiis.tramites.domain.model.TipoTramite;
import com.sgi.fiis.users.domain.model.RolEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TramiteAprobadoEvent {

    private final Long idTramite;
    private final String codigoTramite;
    private final TipoTramite tipoTramite;
    private final Long idSolicitante;
    private final EstadoTramite estadoResultante;
    private final Long idAprobador;
    private final RolEnum rolAprobador;
    private final LocalDateTime fechaAprobacion;
}
