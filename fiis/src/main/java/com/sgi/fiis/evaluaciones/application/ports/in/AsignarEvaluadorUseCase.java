package com.sgi.fiis.evaluaciones.application.ports.in;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;

public interface AsignarEvaluadorUseCase {

    EvaluacionResponse asignarEvaluador(AsignarEvaluadorCommand command);
}