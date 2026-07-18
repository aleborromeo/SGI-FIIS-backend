package com.sgi.fiis.evaluaciones.application.service;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadorUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadoresUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarDetalleAnonimoUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarEvaluacionesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.EvaluarEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.RegistrarResultadoEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import com.sgi.fiis.evaluaciones.domain.exception.EvaluacionException;
import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;
import com.sgi.fiis.evaluaciones.domain.ports.out.EvaluacionRepositoryPort;
import com.sgi.fiis.evaluaciones.presentation.dto.AnonymousProjectDetailResponse;
import com.sgi.fiis.evaluaciones.presentation.dto.EvaluarEvaluacionRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluacionService implements
        AsignarEvaluadorUseCase,
        AsignarEvaluadoresUseCase,
        RegistrarResultadoEvaluacionUseCase,
        ConsultarEvaluacionesUseCase,
        EvaluarEvaluacionUseCase,
        ConsultarDetalleAnonimoUseCase {

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

    @Override
    public List<EvaluacionResponse> asignarEvaluadores(Long projectId, Long planTesisId, List<Long> evaluadorIds) {
        if (evaluadorIds == null || evaluadorIds.isEmpty()) {
            throw new EvaluacionException("Debe indicar al menos un evaluador.");
        }

        List<EvaluacionResponse> responses = new ArrayList<>();
        for (Long idEvaluador : evaluadorIds) {
            AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(
                    projectId, planTesisId, idEvaluador);
            responses.add(asignarEvaluador(command));
        }
        return responses;
    }

    @Override
    public EvaluacionResponse evaluar(Long idEvaluacion, EvaluarEvaluacionRequest request) {
        Evaluacion evaluacion = evaluacionRepositoryPort.buscarPorId(idEvaluacion)
                .orElseThrow(() -> new EvaluacionException("No se encontró la evaluación solicitada."));

        if (!evaluacion.getIdEvaluador().equals(request.evaluatorId())) {
            throw new EvaluacionException("El evaluador no tiene permiso para evaluar esta asignación.");
        }

        ResultadoEvaluacion resultado = switch (request.dictamen() != null ? request.dictamen().toUpperCase() : "") {
            case "APROBADO" -> ResultadoEvaluacion.APROBADO;
            case "APROBADO_CON_OBSERVACIONES" -> ResultadoEvaluacion.CON_OBSERVACIONES;
            case "DESAPROBADO" -> ResultadoEvaluacion.RECHAZADO;
            default -> throw new EvaluacionException("Dictamen no válido: " + request.dictamen());
        };

        evaluacion.registrarResultado(
                resultado,
                request.totalScore(),
                request.observations()
        );

        Evaluacion evaluacionActualizada = evaluacionRepositoryPort.guardar(evaluacion);
        return convertirAResponse(evaluacionActualizada);
    }

    @Override
    public AnonymousProjectDetailResponse consultarDetalleAnonimo(Long idEvaluacion) {
        Evaluacion evaluacion = evaluacionRepositoryPort.buscarPorId(idEvaluacion)
                .orElseThrow(() -> new EvaluacionException("No se encontró la evaluación solicitada."));

        String codigo = evaluacion.perteneceAProyecto()
                ? "PROY-" + evaluacion.getIdProyecto()
                : "TESIS-" + evaluacion.getIdPlanTesis();

        return new AnonymousProjectDetailResponse(
                codigo,
                "Convocatoria " + java.time.Year.now().getValue(),
                "Título del expediente (información anónima)",
                "Resumen técnico del proyecto de investigación.",
                "Objetivo general del proyecto.",
                List.of("Objetivo específico 1", "Objetivo específico 2"),
                50000.0,
                12,
                List.of(
                        new AnonymousProjectDetailResponse.CronogramaItem("Actividad 1", "2026-01-01", "2026-03-01"),
                        new AnonymousProjectDetailResponse.CronogramaItem("Actividad 2", "2026-03-01", "2026-06-01")
                ),
                List.of(
                        new AnonymousProjectDetailResponse.DocumentoItem("documento.pdf", "PDF", "/api/documentos/1")
                ),
                List.of(
                        new AnonymousProjectDetailResponse.CriterioItem(1L, "Pertinencia", "Pertinencia del tema", 20, 0.2),
                        new AnonymousProjectDetailResponse.CriterioItem(2L, "Metodología", "Metodología propuesta", 30, 0.3),
                        new AnonymousProjectDetailResponse.CriterioItem(3L, "Impacto", "Impacto esperado", 25, 0.25),
                        new AnonymousProjectDetailResponse.CriterioItem(4L, "Viabilidad", "Viabilidad del proyecto", 25, 0.25)
                )
        );
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