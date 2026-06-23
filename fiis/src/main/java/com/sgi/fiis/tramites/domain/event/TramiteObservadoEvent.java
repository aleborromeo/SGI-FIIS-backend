package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.TipoTramite;
import com.sgi.fiis.users.domain.model.RoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TramiteObservadoEvent {

    private final Long idTramite;
    private final String codigoTramite;
    private final TipoTramite tipoTramite;
    private final Long idSolicitante;
    private final Long idObservador;
    private final RoleEnum rolObservador;
    private final String textoObservacion;
    private final LocalDateTime fechaObservacion;
}
