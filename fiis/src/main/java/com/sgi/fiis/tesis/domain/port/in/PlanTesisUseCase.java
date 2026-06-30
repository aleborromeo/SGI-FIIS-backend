package pe.unas.fiis.sgifiis.thesis.domain.port.in;

import java.util.List;
import pe.unas.fiis.sgifiis.thesis.application.dto.PlanTesisResponse;
import pe.unas.fiis.sgifiis.thesis.application.dto.RegistrarPlanTesisCommand;
import pe.unas.fiis.sgifiis.thesis.application.dto.ObservarPlanTesisCommand;
import pe.unas.fiis.sgifiis.thesis.application.dto.SubsanarPlanTesisCommand;
import pe.unas.fiis.sgifiis.thesis.application.dto.RegistrarResolucionCommand;
import pe.unas.fiis.sgifiis.thesis.domain.RolRevisor;

public interface PlanTesisUseCase {
    PlanTesisResponse registrarPlan(RegistrarPlanTesisCommand command);
    PlanTesisResponse aprobarPorCoordinador(Integer idPlanTesis);
    PlanTesisResponse observarPorCoordinador(Integer idPlanTesis, ObservarPlanTesisCommand command);
    PlanTesisResponse rechazarPorCoordinador(Integer idPlanTesis, String motivo);
    PlanTesisResponse aprobarPorDirector(Integer idPlanTesis);
    PlanTesisResponse observarPorDirector(Integer idPlanTesis, ObservarPlanTesisCommand command);
    PlanTesisResponse subsanarPlan(Integer idPlanTesis, SubsanarPlanTesisCommand command);
    PlanTesisResponse registrarResolucion(Integer idPlanTesis, RegistrarResolucionCommand command);
    PlanTesisResponse obtenerPorId(Integer idPlanTesis);
    List<PlanTesisResponse> listarPorEstudiante(Long idEstudiante);
    List<PlanTesisResponse> listarPorGrupo(Integer idGrupo);
    List<PlanTesisResponse> listarPendientesPorRevisor(RolRevisor revisor);
}
