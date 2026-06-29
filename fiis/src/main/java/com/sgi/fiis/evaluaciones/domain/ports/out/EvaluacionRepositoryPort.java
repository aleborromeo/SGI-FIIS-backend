package com.sgi.fiis.evaluaciones.domain.ports.out;

import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;

import java.util.List;
import java.util.Optional;

public interface EvaluacionRepositoryPort {

    Evaluacion guardar(Evaluacion evaluacion);

    Optional<Evaluacion> buscarPorId(Long idEvaluacion);

    List<Evaluacion> listarPorEvaluador(Long idEvaluador);

    List<Evaluacion> listarTodas();

    boolean existeEvaluacionPendienteParaProyecto(Long idProyecto, Long idEvaluador);

    boolean existeEvaluacionPendienteParaPlanTesis(Long idPlanTesis, Long idEvaluador);
}