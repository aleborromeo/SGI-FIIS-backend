package com.sgi.fiis.evaluaciones.application.service;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluadorAsignadoResponse;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluadorDisponibleResponse;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadorUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadoresUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarDetalleAnonimoUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarEvaluacionesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.EvaluarEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ListarEvaluadoresDisponiblesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.RegistrarResultadoEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import com.sgi.fiis.evaluaciones.domain.exception.EvaluacionException;
import com.sgi.fiis.evaluaciones.domain.model.Evaluacion;
import com.sgi.fiis.evaluaciones.domain.ports.out.EvaluacionRepositoryPort;
import com.sgi.fiis.evaluaciones.presentation.dto.AnonymousProjectDetailResponse;
import com.sgi.fiis.evaluaciones.presentation.dto.EvaluarEvaluacionRequest;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class EvaluacionService implements
        AsignarEvaluadorUseCase,
        AsignarEvaluadoresUseCase,
        RegistrarResultadoEvaluacionUseCase,
        ConsultarEvaluacionesUseCase,
        EvaluarEvaluacionUseCase,
        ConsultarDetalleAnonimoUseCase,
        ListarEvaluadoresDisponiblesUseCase {

    private static final String NO_ENCONTRADA = "No se encontró la evaluación solicitada.";
    private static final Set<String> EVALUADOR_ROLES = Set.of("EVALUADOR", "DOCENTE_INVESTIGADOR", "COORDINADOR_GRUPO");

    private final EvaluacionRepositoryPort evaluacionRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final JdbcTemplate jdbcTemplate;

    public EvaluacionService(EvaluacionRepositoryPort evaluacionRepositoryPort,
                             UserRepositoryPort userRepositoryPort,
                             JdbcTemplate jdbcTemplate) {
        this.evaluacionRepositoryPort = evaluacionRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Auditable(action = "ASSIGN_EVALUATOR", table = "evaluaciones")
    public EvaluacionResponse asignarEvaluador(AsignarEvaluadorCommand command) {
        validarAsignacion(command);
        validarEvaluadorRole(command.idEvaluador());

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
    @Auditable(action = "REGISTER_EVALUATION_RESULT", table = "evaluaciones")
    public EvaluacionResponse registrarResultado(RegistrarResultadoEvaluacionCommand command) {
        validarResultado(command);

        Evaluacion evaluacion = evaluacionRepositoryPort.buscarPorId(command.idEvaluacion())
                .orElseThrow(() -> new EvaluacionException(NO_ENCONTRADA));

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
                .orElseThrow(() -> new EvaluacionException(NO_ENCONTRADA));
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
    @Auditable(action = "EVALUATE", table = "evaluaciones")
    public EvaluacionResponse evaluar(Long idEvaluacion, EvaluarEvaluacionRequest request) {
        Evaluacion evaluacion = evaluacionRepositoryPort.buscarPorId(idEvaluacion)
                .orElseThrow(() -> new EvaluacionException(NO_ENCONTRADA));

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
                .orElseThrow(() -> new EvaluacionException(NO_ENCONTRADA));

        String codigo = evaluacion.perteneceAProyecto()
                ? "PROY-" + evaluacion.getIdProyecto()
                : "TESIS-" + evaluacion.getIdPlanTesis();

        return AnonymousProjectDetailResponse.placeholder(codigo);
    }

    @Override
    public List<EvaluadorDisponibleResponse> execute(Long projectId) {
        String sql;
        List<Map<String, Object>> rows;

        if (projectId != null) {
            Long groupId = jdbcTemplate.queryForObject(
                    "SELECT id_grupo FROM proyectos WHERE id_proyecto = ?", Long.class, projectId);

            if (groupId == null) {
                return List.of();
            }

            sql = """
                    SELECT DISTINCT u.id_usuario, u.nombres, u.apellidos, u.correo_institucional,
                           r.codigo_rol, r.descripcion
                    FROM usuarios u
                    INNER JOIN roles r ON u.id_rol_principal = r.id_rol
                    INNER JOIN membresias_grupo mg ON u.id_usuario = mg.id_usuario
                    WHERE u.es_activo = TRUE
                      AND mg.id_grupo = ? AND mg.es_activo = TRUE
                      AND r.codigo_rol IN ('EVALUADOR', 'DOCENTE_INVESTIGADOR', 'COORDINADOR_GRUPO')
                    ORDER BY u.nombres, u.apellidos
                    """;
            rows = jdbcTemplate.queryForList(sql, groupId);
        } else {
            sql = """
                    SELECT DISTINCT u.id_usuario, u.nombres, u.apellidos, u.correo_institucional,
                           r.codigo_rol, r.descripcion
                    FROM usuarios u
                    INNER JOIN roles r ON u.id_rol_principal = r.id_rol
                    WHERE u.es_activo = TRUE
                      AND r.codigo_rol IN ('EVALUADOR', 'DOCENTE_INVESTIGADOR', 'COORDINADOR_GRUPO')
                    ORDER BY u.nombres, u.apellidos
                    """;
            rows = jdbcTemplate.queryForList(sql);
        }

        return rows.stream()
                .map(row -> new EvaluadorDisponibleResponse(
                        ((Number) row.get("id_usuario")).longValue(),
                        (String) row.get("nombres"),
                        (String) row.get("apellidos"),
                        (String) row.get("correo_institucional"),
                        (String) row.get("codigo_rol"),
                        (String) row.get("descripcion")
                ))
                .toList();
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

    private void validarEvaluadorRole(Long idEvaluador) {
        Optional<User> userOpt = userRepositoryPort.findById(idEvaluador);
        if (userOpt.isEmpty()) {
            throw new EvaluacionException("El usuario evaluador no existe.");
        }
        User user = userOpt.get();
        if (!EVALUADOR_ROLES.contains(user.getRoleCode())) {
            throw new EvaluacionException(
                    "El usuario no tiene un rol válido para evaluación. Rol actual: " + user.getRoleCode());
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

    @Override
    public List<EvaluadorAsignadoResponse> listarEvaluadoresPorProyecto(Long idProyecto) {
        if (idProyecto == null) {
            return List.of();
        }
        String sql = """
                SELECT e.id_evaluador, e.resultado, e.fecha_evaluacion,
                       u.nombres, u.apellidos, u.correo_institucional, r.codigo_rol
                FROM evaluaciones e
                INNER JOIN usuarios u ON e.id_evaluador = u.id_usuario
                INNER JOIN roles r ON u.id_rol_principal = r.id_rol
                WHERE e.id_proyecto = ?
                ORDER BY u.nombres, u.apellidos
                """;
        return jdbcTemplate.queryForList(sql, idProyecto).stream()
                .map(row -> new EvaluadorAsignadoResponse(
                        ((Number) row.get("id_evaluador")).longValue(),
                        (String) row.get("nombres"),
                        (String) row.get("apellidos"),
                        (String) row.get("correo_institucional"),
                        (String) row.get("codigo_rol"),
                        (String) row.get("resultado"),
                        row.get("fecha_evaluacion") == null
                ))
                .toList();
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
        String proyectoTitulo = null;
        String proyectoResumen = null;
        String planTesisTitulo = null;
        String planTesisResumen = null;

        if (evaluacion.getIdProyecto() != null) {
            try {
                var projectRow = jdbcTemplate.queryForMap(
                        "SELECT titulo_proyecto, resumen FROM proyectos WHERE id_proyecto = ?",
                        evaluacion.getIdProyecto()
                );
                proyectoTitulo = (String) projectRow.get("titulo_proyecto");
                proyectoResumen = (String) projectRow.get("resumen");
            } catch (Exception ignored) {
                // Ignored because project details are optional in the response representation
            }
        }

        if (evaluacion.getIdPlanTesis() != null) {
            try {
                var thesisRow = jdbcTemplate.queryForMap(
                        "SELECT titulo_tesis, resumen FROM planes_tesis WHERE id_plan_tesis = ?",
                        evaluacion.getIdPlanTesis()
                );
                planTesisTitulo = (String) thesisRow.get("titulo_tesis");
                planTesisResumen = (String) thesisRow.get("resumen");
            } catch (Exception ignored) {
                // Thesis plan might not exist or fields are null
            }
        }

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
                evaluacion.estaPendiente(),
                proyectoTitulo,
                proyectoResumen,
                planTesisTitulo,
                planTesisResumen
        );
    }
}