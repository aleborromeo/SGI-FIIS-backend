package com.sgi.fiis.tramites.domain.event;

import com.sgi.fiis.tramites.domain.model.TipoTramite;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TramiteFinalizadoEvent {

    private final Long idTramite;
    private final String codigoTramite;
    private final TipoTramite tipoTramite;
    private final Long idSolicitante;
    private final LocalDateTime fechaFinalizacion;
}
