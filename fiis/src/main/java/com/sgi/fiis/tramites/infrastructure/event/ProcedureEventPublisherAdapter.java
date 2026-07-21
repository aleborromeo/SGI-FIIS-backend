package com.sgi.fiis.tramites.infrastructure.event;

import com.sgi.fiis.tramites.domain.event.ProcedureApprovedEvent;
import com.sgi.fiis.tramites.domain.event.ProcedureFinalizedEvent;
import com.sgi.fiis.tramites.domain.event.ProcedureFlaggedEvent;
import com.sgi.fiis.tramites.domain.port.ProcedureEventPublisherPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcedureEventPublisherAdapter implements ProcedureEventPublisherPort {

    @Override
    public void publishProcedureApproved(ProcedureApprovedEvent evento) {
        log.info("[EVENTO] TramiteAprobado: tramite={}, estadoResultante={}, rol={}",
                evento.getCode(), evento.getResultingStatus(), evento.getApproverRole());
    }

    @Override
    public void publishProcedureFlagged(ProcedureFlaggedEvent evento) {
        log.info("[EVENTO] TramiteObservado: tramite={}, rol={}",
                evento.getCode(), evento.getObserverRole());
    }

    @Override
    public void publishProcedureFinalized(ProcedureFinalizedEvent evento) {
        log.info("[EVENTO] TramiteFinalizado: tramite={}, tipo={}",
                evento.getCode(), evento.getProcedureType());
    }
}
