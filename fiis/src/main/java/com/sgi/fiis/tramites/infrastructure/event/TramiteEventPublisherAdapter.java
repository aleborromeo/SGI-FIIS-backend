package com.sgi.fiis.tramites.infrastructure.event;

import com.sgi.fiis.tramites.domain.event.TramiteAprobadoEvent;
import com.sgi.fiis.tramites.domain.event.TramiteFinalizadoEvent;
import com.sgi.fiis.tramites.domain.event.TramiteObservadoEvent;
import com.sgi.fiis.tramites.domain.port.TramiteEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TramiteEventPublisherAdapter implements TramiteEventPublisherPort {

    @Override
    public void publicarTramiteAprobado(TramiteAprobadoEvent evento) {
        log.info("[EVENTO] TramiteAprobado: tramite={}, estadoResultante={}, rol={}",
                evento.getCodigoTramite(), evento.getEstadoResultante(), evento.getRolAprobador());
    }

    @Override
    public void publicarTramiteObservado(TramiteObservadoEvent evento) {
        log.info("[EVENTO] TramiteObservado: tramite={}, rol={}",
                evento.getCodigoTramite(), evento.getRolObservador());
    }

    @Override
    public void publicarTramiteFinalizado(TramiteFinalizadoEvent evento) {
        log.info("[EVENTO] TramiteFinalizado: tramite={}, tipo={}",
                evento.getCodigoTramite(), evento.getTipoTramite());
    }
}
