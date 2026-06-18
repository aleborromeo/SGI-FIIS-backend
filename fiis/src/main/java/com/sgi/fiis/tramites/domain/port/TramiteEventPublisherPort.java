package com.sgi.fiis.tramites.domain.port;

import com.sgi.fiis.tramites.domain.event.TramiteAprobadoEvent;
import com.sgi.fiis.tramites.domain.event.TramiteFinalizadoEvent;
import com.sgi.fiis.tramites.domain.event.TramiteObservadoEvent;

public interface TramiteEventPublisherPort {

    void publicarTramiteAprobado(TramiteAprobadoEvent evento);

    void publicarTramiteObservado(TramiteObservadoEvent evento);

    void publicarTramiteFinalizado(TramiteFinalizadoEvent evento);
}
