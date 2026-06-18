package com.sgi.fiis.observaciones.domain.port;

import com.sgi.fiis.observaciones.domain.model.Correction;
import java.util.List;
import java.util.Optional;

/**
 * Output port (domain exit port) for correction persistence.
 */
public interface CorrectionRepositoryPort {
    Correction save(Correction correction);
    Optional<Correction> findById(Integer id);
    List<Correction> findByObservationId(Integer observationId);
}
