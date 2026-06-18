package pe.unas.fiis.sgifiis.thesis.domain.port.in;

import java.util.List;
import pe.unas.fiis.sgifiis.thesis.application.dto.InformeTesisResponse;
import pe.unas.fiis.sgifiis.thesis.application.dto.RegistrarInformeTesisCommand;

public interface InformeTesisUseCase {
    InformeTesisResponse registrarInformeFinal(RegistrarInformeTesisCommand command);
    InformeTesisResponse aprobarInforme(Integer idInformeTesis, Integer idUsuarioAccion);
    InformeTesisResponse observarInforme(Integer idInformeTesis, Integer idUsuarioAccion, String observacion);
    InformeTesisResponse obtenerPorId(Integer idInformeTesis);
    List<InformeTesisResponse> listarPorPlan(Integer idPlanTesis);
}
