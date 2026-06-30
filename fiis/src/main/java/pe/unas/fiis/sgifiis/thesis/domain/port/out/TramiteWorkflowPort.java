package pe.unas.fiis.sgifiis.thesis.domain.port.out;

import java.time.LocalDate;
import java.util.List;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoTramiteTesis;
import pe.unas.fiis.sgifiis.thesis.domain.RolRevisor;

public interface TramiteWorkflowPort {
    Integer crearTramitePlanTesis(Integer idPlanTesis, Long idSolicitante, Integer idGrupo);
    void derivarPlanTesis(Integer idPlanTesis, Long idUsuarioAccion, EstadoTramiteTesis estadoNuevo,
                          RolRevisor rolNuevo, String accion, String observacion, Integer idDocumentoAdjunto);
    Integer obtenerIdTramitePorPlanTesis(Integer idPlanTesis);
    String obtenerEstadoTramitePorPlanTesis(Integer idPlanTesis);
    String obtenerRevisorTramitePorPlanTesis(Integer idPlanTesis);
    List<Integer> findPlanTesisIdsByRevisor(RolRevisor rolRevisor);
    Integer registrarResolucion(Integer idPlanTesis, Long idUsuarioAccion,
                                String numeroResolucion, LocalDate fechaEmision,
                                String asunto, Integer idDocumentoAdjunto);
}
