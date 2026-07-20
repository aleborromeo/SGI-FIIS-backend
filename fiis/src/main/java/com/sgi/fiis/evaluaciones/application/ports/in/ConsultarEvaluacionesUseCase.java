package com.sgi.fiis.evaluaciones.application.ports.in;

import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluadorAsignadoResponse;

import java.util.List;

public interface ConsultarEvaluacionesUseCase {

    List<EvaluacionResponse> listarPorEvaluador(Long idEvaluador);

    List<EvaluacionResponse> listarTodas();

    EvaluacionResponse buscarPorId(Long idEvaluacion);

    List<EvaluadorAsignadoResponse> listarEvaluadoresPorProyecto(Long idProyecto);
}