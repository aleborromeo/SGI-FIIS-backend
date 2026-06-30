package com.sgi.fiis.tramites.domain.port;

import com.sgi.fiis.tramites.domain.event.ProcedureApprovedEvent;
import com.sgi.fiis.tramites.domain.event.ProcedureFinalizedEvent;
import com.sgi.fiis.tramites.domain.event.ProcedureFlaggedEvent;

public interface ProcedureEventPublisherPort {

    void publishProcedureApproved(ProcedureApprovedEvent evento);

    void publishProcedureFlagged(ProcedureFlaggedEvent evento);

    void publishProcedureFinalized(ProcedureFinalizedEvent evento);
}
