package com.sgi.fiis.thesis.domain.port.out;

import java.time.LocalDate;
import java.util.List;
import com.sgi.fiis.thesis.domain.ThesisProcedureStatus;
import com.sgi.fiis.thesis.domain.ReviewerRole;

public interface ProcedureWorkflowPort {
    Integer crearTramitePlanTesis(Integer idPlanTesis, Long idSolicitante, Integer idGrupo);
    void derivarPlanTesis(Integer idPlanTesis, Long idUsuarioAccion, ThesisProcedureStatus estadoNuevo,
                          ReviewerRole rolNuevo, String accion, String observacion, Integer idDocumentoAdjunto);
    Integer obtenerIdTramitePorPlanTesis(Integer idPlanTesis);
    String obtenerEstadoTramitePorPlanTesis(Integer idPlanTesis);
    String obtenerRevisorTramitePorPlanTesis(Integer idPlanTesis);
    List<Integer> findPlanTesisIdsByRevisor(ReviewerRole rolRevisor);
    Integer registrarResolucion(Integer idPlanTesis, Long idUsuarioAccion,
                                String numeroResolucion, LocalDate fechaEmision,
                                String asunto, Integer idDocumentoAdjunto);
}
