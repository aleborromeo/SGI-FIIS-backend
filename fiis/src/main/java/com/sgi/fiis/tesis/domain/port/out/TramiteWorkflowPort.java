package pe.unas.fiis.sgifiis.thesis.domain.port.out;

import pe.unas.fiis.sgifiis.thesis.domain.EstadoTramiteTesis;
import pe.unas.fiis.sgifiis.thesis.domain.RolRevisor;

public interface TramiteWorkflowPort {
    Integer crearTramitePlanTesis(Integer idPlanTesis, Integer idSolicitante, Integer idGrupo);
    void derivarPlanTesis(Integer idPlanTesis, Integer idUsuarioAccion, EstadoTramiteTesis estadoNuevo,
                          RolRevisor rolNuevo, String accion, String observacion, Integer idDocumentoAdjunto);
    Integer obtenerIdTramitePorPlanTesis(Integer idPlanTesis);
}
