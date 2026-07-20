package com.sgi.fiis.evaluaciones.application.ports.in;

import com.sgi.fiis.evaluaciones.application.dto.response.EvaluadorDisponibleResponse;

import java.util.List;

public interface ListarEvaluadoresDisponiblesUseCase {
    List<EvaluadorDisponibleResponse> execute(Long projectId);
}
