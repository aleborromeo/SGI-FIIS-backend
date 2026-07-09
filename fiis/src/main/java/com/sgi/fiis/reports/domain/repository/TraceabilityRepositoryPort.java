package com.sgi.fiis.reports.domain.repository;

import com.sgi.fiis.reports.domain.model.TraceabilityMovement;

import java.util.List;

/**
 * Contract for the procedure traceability repository.
 * Defined in domain to apply Dependency Inversion.
 */
public interface TraceabilityRepositoryPort {

    /**
     * Returns all movements of a procedure in ascending chronological order.
     */
    List<TraceabilityMovement> findByProcedureId(Integer procedureId);
}
