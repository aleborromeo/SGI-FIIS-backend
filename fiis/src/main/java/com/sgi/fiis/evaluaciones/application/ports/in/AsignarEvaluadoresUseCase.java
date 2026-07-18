package com.sgi.fiis.evaluaciones.application.ports.in;

import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import java.util.List;

public interface AsignarEvaluadoresUseCase {
    List<EvaluacionResponse> asignarEvaluadores(Long projectId, Long planTesisId, List<Long> evaluadorIds);
}
