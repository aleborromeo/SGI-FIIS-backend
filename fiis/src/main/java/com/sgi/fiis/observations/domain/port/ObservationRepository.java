package com.sgi.fiis.observations.domain.port;

import com.sgi.fiis.observations.domain.model.Observation;
import java.util.List;
import java.util.Optional;

/**
 * Output port for the persistence of observations.
 */
public interface ObservationRepository {
    Observation save(Observation observation);
    Optional<Observation> findById(Integer id);
    List<Observation> findByProcedureId(Integer procedureId);
    List<Observation> findPendingByProcedureId(Integer procedureId);
}
