package com.sgi.fiis.reports.application.service;

import com.sgi.fiis.reports.domain.model.ProcedureRecentActivity;
import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
import com.sgi.fiis.reports.domain.repository.TraceabilityRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraceabilityService {

    private final TraceabilityRepositoryPort repo;

    public TraceabilityService(TraceabilityRepositoryPort repo) {
        this.repo = repo;
    }

    /**
     * Retrieves all movements of a procedure in ascending chronological order.
     * For COORDINADOR_GRUPO users, only returns data if the procedure belongs to their group.
     *
     * @param procedureId the ID of the procedure to query
     * @param userGroupId the research group ID of the current user (null for ADMIN/DIRECTOR)
     * @return empty list if the procedure does not exist, has no movements, or access is denied
     * @throws SecurityException if the user does not have permission to view this procedure
     */
    public List<TraceabilityMovement> getTraceability(Integer procedureId, Integer userGroupId) {
        if (userGroupId != null) {
            boolean belongs = repo.isProcedureInGroup(procedureId, userGroupId);
            if (!belongs) {
                throw new SecurityException("Acceso denegado: el trámite no pertenece a su grupo de investigación");
            }
        }
        return repo.findByProcedureId(procedureId);
    }

    public List<TraceabilityMovement> getTraceability(Integer procedureId) {
        return getTraceability(procedureId, null);
    }

    /**
     * Returns procedures with recent activity.
     * For COORDINADOR_GRUPO users, only returns procedures from their group.
     *
     * @param userGroupId the research group ID of the current user (null for ADMIN/DIRECTOR)
     * @param days number of days to look back
     * @return list of procedures with recent movements
     */
    public List<ProcedureRecentActivity> getProceduresWithRecentActivity(Integer userGroupId, int days) {
        return repo.findProceduresWithRecentActivity(userGroupId, days);
    }
}
