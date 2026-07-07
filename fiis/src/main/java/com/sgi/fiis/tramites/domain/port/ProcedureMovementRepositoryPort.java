package com.sgi.fiis.tramites.domain.port;

import com.sgi.fiis.tramites.domain.model.ProcedureMovement;

import java.util.List;

public interface ProcedureMovementRepositoryPort {

    List<ProcedureMovement> findByProcedureId(Long idTramite);
}
