package com.sgi.fiis.reports.domain.repository;

import com.sgi.fiis.reports.domain.model.ProcedureRecentActivity;
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

    /**
     * Checks if a procedure belongs to a specific research group.
     * Used to enforce COORDINADOR_GRUPO access restrictions.
     */
    boolean isProcedureInGroup(Integer procedureId, Integer groupId);

    /**
     * Returns procedures with recent activity (last N days).
     * @param groupId If provided, filter by research group (for COORDINADOR_GRUPO)
     * @param days Number of days to look back
     * @return List of procedures with recent movements
     */
    List<ProcedureRecentActivity> findProceduresWithRecentActivity(Integer groupId, int days);
}
