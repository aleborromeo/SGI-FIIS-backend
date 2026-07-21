package com.sgi.fiis.evaluaciones.application.ports.in;

import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.presentation.dto.EvaluarEvaluacionRequest;

public interface EvaluarEvaluacionUseCase {
    EvaluacionResponse evaluar(Long idEvaluacion, EvaluarEvaluacionRequest request);
}
