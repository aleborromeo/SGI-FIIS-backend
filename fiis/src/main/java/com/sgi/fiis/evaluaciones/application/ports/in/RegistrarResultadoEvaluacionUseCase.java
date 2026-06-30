package com.sgi.fiis.evaluaciones.application.ports.in;

import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;

public interface RegistrarResultadoEvaluacionUseCase {

    EvaluacionResponse registrarResultado(RegistrarResultadoEvaluacionCommand command);
}