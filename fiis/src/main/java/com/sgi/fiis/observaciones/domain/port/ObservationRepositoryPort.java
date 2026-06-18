package com.sgi.fiis.observaciones.domain.port;

import com.sgi.fiis.observaciones.domain.model.Observation;
import java.util.List;
import java.util.Optional;

/**
 * Output port (domain exit port) for observation persistence.
 */
public interface ObservationRepositoryPort {
    Observation save(Observation observation);
    Optional<Observation> findById(Integer id);
    List<Observation> findByProcedureId(Integer procedureId);
    List<Observation> findPendingByProcedureId(Integer procedureId);
}
