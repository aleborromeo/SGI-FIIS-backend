package com.sgi.fiis.observations.domain.port;

import com.sgi.fiis.observations.domain.model.Remedy;
import java.util.List;
import java.util.Optional;

/**
 * Output port for the persistence of remedies.
 */
public interface RemedyRepository {
    Remedy save(Remedy remedy);
    Optional<Remedy> findById(Integer id);
    List<Remedy> findByObservationId(Integer observationId);
}
