package com.sgi.fiis.evaluaciones.application.service;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadorUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarEvaluacionesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.RegistrarResultadoEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.domain.exception.EvaluacionException;
import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;
import com.sgi.fiis.evaluaciones.domain.ports.out.EvaluacionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EvaluacionService implements
        AsignarEvaluadorUseCase,
        RegistrarResultadoEvaluacionUseCase,
        ConsultarEvaluacionesUseCase {

    private final EvaluacionRepositoryPort evaluacionRepositoryPort;

    public EvaluacionService(EvaluacionRepositoryPort evaluacionRepositoryPort) {
        this.evaluacionRepositoryPort = evaluacionRepositoryPort;
    }

    @Override
    public EvaluacionResponse asignarEvaluador(AsignarEvaluadorCommand command) {
        validarAsignacion(command);

        Evaluacion evaluacion;

        if (command.idProyecto() != null) {
            validarDuplicidadProyecto(command.idProyecto(), command.idEvaluador());
            evaluacion = Evaluacion.asignarAProyecto(command.idProyecto(), command.idEvaluador());
        } else {
            validarDuplicidadPlanTesis(command.idPlanTesis(), command.idEvaluador());
            evaluacion = Evaluacion.asignarAPlanTesis(command.idPlanTesis(), command.idEvaluador());
        }

        Evaluacion evaluacionGuardada = evaluacionRepositoryPort.guardar(evaluacion);

        return convertirAResponse(evaluacionGuardada);
    }

    @Override
    public EvaluacionResponse registrarResultado(RegistrarResultadoEvaluacionCommand command) {
        validarResultado(command);

        Evaluacion evaluacion = evaluacionRepositoryPort.buscarPorId(command.idEvaluacion())
                .orElseThrow(() -> new EvaluacionException("No se encontró la evaluación solicitada."));

        if (!evaluacion.getIdEvaluador().equals(command.idEvaluador())) {
            throw new EvaluacionException("El evaluador no tiene permiso para registrar esta evaluación.");
        }

        evaluacion.registrarResultado(
                command.resultado(),
                command.puntaje(),
                command.observaciones()
        );

        Evaluacion evaluacionActualizada = evaluacionRepositoryPort.guardar(evaluacion);

        return convertirAResponse(evaluacionActualizada);
    }

    @Override
    public List<EvaluacionResponse> listarPorEvaluador(Long idEvaluador) {
        if (idEvaluador == null) {
            throw new EvaluacionException("El identificador del evaluador es obligatorio.");
        }

        return evaluacionRepositoryPort.listarPorEvaluador(idEvaluador)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    public List<EvaluacionResponse> listarTodas() {
        return evaluacionRepositoryPort.listarTodas()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Override
    public EvaluacionResponse buscarPorId(Long idEvaluacion) {
        if (idEvaluacion == null) {
            throw new EvaluacionException("El identificador de la evaluación es obligatorio.");
        }

        return evaluacionRepositoryPort.buscarPorId(idEvaluacion)
                .map(this::convertirAResponse)
                .orElseThrow(() -> new EvaluacionException("No se encontró la evaluación solicitada."));
    }

    private void validarAsignacion(AsignarEvaluadorCommand command) {
        if (command == null) {
            throw new EvaluacionException("La solicitud de asignación no puede ser nula.");
        }

        if (command.idEvaluador() == null) {
            throw new EvaluacionException("El evaluador es obligatorio.");
        }

        boolean tieneProyecto = command.idProyecto() != null;
        boolean tienePlanTesis = command.idPlanTesis() != null;

        if (tieneProyecto && tienePlanTesis) {
            throw new EvaluacionException("Debe asignar la evaluación a un proyecto o a un plan de tesis, no a ambos.");
        }

        if (!tieneProyecto && !tienePlanTesis) {
            throw new EvaluacionException("Debe indicar un proyecto o un plan de tesis para la evaluación.");
        }
    }

    private void validarResultado(RegistrarResultadoEvaluacionCommand command) {
        if (command == null) {
            throw new EvaluacionException("La solicitud de resultado no puede ser nula.");
        }

        if (command.idEvaluacion() == null) {
            throw new EvaluacionException("El identificador de la evaluación es obligatorio.");
        }

        if (command.idEvaluador() == null) {
            throw new EvaluacionException("El identificador del evaluador es obligatorio.");
        }

        if (command.resultado() == null) {
            throw new EvaluacionException("El resultado de la evaluación es obligatorio.");
        }
    }

    private void validarDuplicidadProyecto(Long idProyecto, Long idEvaluador) {
        boolean existePendiente = evaluacionRepositoryPort
                .existeEvaluacionPendienteParaProyecto(idProyecto, idEvaluador);

        if (existePendiente) {
            throw new EvaluacionException("El evaluador ya tiene una evaluación pendiente para este proyecto.");
        }
    }

    private void validarDuplicidadPlanTesis(Long idPlanTesis, Long idEvaluador) {
        boolean existePendiente = evaluacionRepositoryPort
                .existeEvaluacionPendienteParaPlanTesis(idPlanTesis, idEvaluador);

        if (existePendiente) {
            throw new EvaluacionException("El evaluador ya tiene una evaluación pendiente para este plan de tesis.");
        }
    }

    private EvaluacionResponse convertirAResponse(Evaluacion evaluacion) {
        return new EvaluacionResponse(
                evaluacion.getIdEvaluacion(),
                evaluacion.getIdProyecto(),
                evaluacion.getIdPlanTesis(),
                evaluacion.getIdEvaluador(),
                evaluacion.getResultado(),
                evaluacion.getPuntaje(),
                evaluacion.getObservaciones(),
                evaluacion.getFechaAsignacion(),
                evaluacion.getFechaEvaluacion(),
                evaluacion.estaPendiente()
        );
    }
}