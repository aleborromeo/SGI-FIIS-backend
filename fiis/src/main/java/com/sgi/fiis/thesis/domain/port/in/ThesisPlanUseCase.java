package com.sgi.fiis.thesis.domain.port.in;

import java.util.List;
import com.sgi.fiis.thesis.application.dto.ThesisPlanResponse;
import com.sgi.fiis.thesis.application.dto.RegisterThesisPlanCommand;
import com.sgi.fiis.thesis.application.dto.ObserveThesisPlanCommand;
import com.sgi.fiis.thesis.application.dto.RectifyThesisPlanCommand;
import com.sgi.fiis.thesis.application.dto.RegisterResolutionCommand;
import com.sgi.fiis.thesis.domain.ReviewerRole;

public interface ThesisPlanUseCase {
    ThesisPlanResponse registrarPlan(RegisterThesisPlanCommand command);
    ThesisPlanResponse aprobarPorCoordinador(Integer idPlanTesis);
    ThesisPlanResponse observarPorCoordinador(Integer idPlanTesis, ObserveThesisPlanCommand command);
    ThesisPlanResponse rechazarPorCoordinador(Integer idPlanTesis, String motivo);
    ThesisPlanResponse aprobarPorDirector(Integer idPlanTesis);
    ThesisPlanResponse observarPorDirector(Integer idPlanTesis, ObserveThesisPlanCommand command);
    ThesisPlanResponse subsanarPlan(Integer idPlanTesis, RectifyThesisPlanCommand command);
    ThesisPlanResponse registrarResolucion(Integer idPlanTesis, RegisterResolutionCommand command);
    ThesisPlanResponse obtenerPorId(Integer idPlanTesis);
    List<ThesisPlanResponse> listarPorEstudiante(Long idEstudiante);
    List<ThesisPlanResponse> listarPorGrupo(Integer idGrupo);
    List<ThesisPlanResponse> listarPendientesPorRevisor(ReviewerRole revisor);
}
