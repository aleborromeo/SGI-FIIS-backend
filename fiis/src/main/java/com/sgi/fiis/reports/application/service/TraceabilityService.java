package com.sgi.fiis.reports.application.service;

import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
import com.sgi.fiis.reports.domain.repository.TraceabilityRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for procedure traceability.
 * Returns the chronological history of movements for a specific procedure.
 */
@Service
public class TraceabilityService {

    private final TraceabilityRepositoryPort repo;

    public TraceabilityService(TraceabilityRepositoryPort repo) {
        this.repo = repo;
    }

    /**
     * Retrieves all movements of a procedure in ascending chronological order.
     * Includes: user, action, previous/new status, observation, and date.
     *
     * @param procedureId the ID of the procedure to query
     * @return empty list if the procedure does not exist or has no recorded movements
     */
    public List<TraceabilityMovement> getTraceability(Integer procedureId) {
        return repo.findByProcedureId(procedureId);
    }
}
