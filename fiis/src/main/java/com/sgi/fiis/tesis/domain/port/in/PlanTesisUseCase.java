package pe.unas.fiis.sgifiis.thesis.domain.port.in;

import java.util.List;
import pe.unas.fiis.sgifiis.thesis.application.dto.PlanTesisResponse;
import pe.unas.fiis.sgifiis.thesis.application.dto.RegistrarPlanTesisCommand;
import pe.unas.fiis.sgifiis.thesis.application.dto.ObservarPlanTesisCommand;
import pe.unas.fiis.sgifiis.thesis.application.dto.SubsanarPlanTesisCommand;

public interface PlanTesisUseCase {
    PlanTesisResponse registrarPlan(RegistrarPlanTesisCommand command);
    PlanTesisResponse aprobarPorCoordinador(Integer idPlanTesis, Integer idUsuarioAccion);
    PlanTesisResponse observarPorCoordinador(Integer idPlanTesis, ObservarPlanTesisCommand command);
    PlanTesisResponse rechazarPorCoordinador(Integer idPlanTesis, Integer idUsuarioAccion, String motivo);
    PlanTesisResponse aprobarPorDirector(Integer idPlanTesis, Integer idUsuarioAccion);
    PlanTesisResponse observarPorDirector(Integer idPlanTesis, ObservarPlanTesisCommand command);
    PlanTesisResponse subsanarPlan(Integer idPlanTesis, SubsanarPlanTesisCommand command);
    PlanTesisResponse obtenerPorId(Integer idPlanTesis);
    List<PlanTesisResponse> listarPorEstudiante(Integer idEstudiante);
    List<PlanTesisResponse> listarPorGrupo(Integer idGrupo);
}
