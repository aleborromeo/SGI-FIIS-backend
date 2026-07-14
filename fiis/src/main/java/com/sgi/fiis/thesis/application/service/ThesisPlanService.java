package com.sgi.fiis.thesis.application.service;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.*;
import com.sgi.fiis.thesis.domain.exception.*;
import com.sgi.fiis.thesis.domain.port.in.ThesisPlanUseCase;
import com.sgi.fiis.thesis.domain.port.out.*;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;

@Service
@Transactional
public class ThesisPlanService implements ThesisPlanUseCase {
    private static final String ROLE_ESTUDIANTE = "ROLE_ESTUDIANTE";
    private static final String ROLE_COORDINADOR_GRUPO = "ROLE_COORDINADOR_GRUPO";
    private static final String ROLE_DIRECTOR_INVESTIGACION = "ROLE_DIRECTOR_INVESTIGACION";
    private static final String ROLE_DECANO = "ROLE_DECANO";
    private static final String MSG_USUARIO_NO_AUTENTICADO = "No se pudo identificar al usuario autenticado";
    private static final Logger log = LoggerFactory.getLogger(ThesisPlanService.class);

    private final ThesisPlanRepositoryPort planRepository;
    private final ProcedureWorkflowPort tramiteWorkflow;
    private final DocumentValidationPort documentoValidation;
    private final ResearchGroupValidationPort grupoValidation;
    private final JdbcTemplate jdbcTemplate;

    public ThesisPlanService(ThesisPlanRepositoryPort planRepository,
                            ProcedureWorkflowPort tramiteWorkflow,
                            DocumentValidationPort documentoValidation,
                            ResearchGroupValidationPort grupoValidation,
                            JdbcTemplate jdbcTemplate) {
        this.planRepository = planRepository;
        this.tramiteWorkflow = tramiteWorkflow;
        this.documentoValidation = documentoValidation;
        this.grupoValidation = grupoValidation;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ThesisPlanResponse registrarPlan(RegisterThesisPlanCommand command) {
        Long idEstudiante = extraerIdEstudianteDelContexto();
        validarGrupoLineaYDocumento(command.idGrupo(), command.idLinea(), command.idDocumentoActual(), idEstudiante);
        ThesisPlan guardado = planRepository.save(ThesisPlan.nuevo(
                command.tituloTesis(), command.resumen(), idEstudiante, command.idLinea(),
                command.idGrupo(), command.idDocumentoActual()));
        Integer idTramite = tramiteWorkflow.crearTramitePlanTesis(guardado.getIdPlanTesis(), guardado.getIdEstudiante(), guardado.getIdGrupo());
        return toResponse(guardado, idTramite);
    }

    private Long extraerIdEstudianteDelContexto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esEstudiante = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_ESTUDIANTE));
            if (!esEstudiante) {
                throw new BusinessRuleViolationException("Solo los estudiantes pueden registrar un plan de tesis");
            }
            return userDetails.getId();
        }
        throw new BusinessRuleViolationException("No se pudo identificar al estudiante autenticado");
    }

    @Override
    public ThesisPlanResponse aprobarPorCoordinador(Integer idPlanTesis) {
        ThesisPlan plan = obtenerPlan(idPlanTesis);
        validarRolCoordinador();
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        if (!grupoValidation.esCoordinadorDelGrupo(idUsuarioAccion, plan.getIdGrupo())) {
            throw new BusinessRuleViolationException("No tiene permisos para gestionar planes de tesis fuera de su grupo de investigación");
        }
        if (plan.getEstadoPlan() != ThesisPlanStatus.POSTULADO) {
            throw new BusinessRuleViolationException("Solo se pueden aprobar planes en estado POSTULADO");
        }
        plan.marcarAprobado();
        ThesisPlan guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, ThesisProcedureStatus.PENDIENTE_DIRECCION,
                ReviewerRole.DIRECTOR_INVESTIGACION, "APROBAR_COORDINADOR", null, null);
        return toResponse(guardado);
    }

    @Override
    public ThesisPlanResponse observarPorCoordinador(Integer idPlanTesis, ObserveThesisPlanCommand command) {
        ThesisPlan plan = obtenerPlan(idPlanTesis);
        validarRolCoordinador();
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        if (!grupoValidation.esCoordinadorDelGrupo(idUsuarioAccion, plan.getIdGrupo())) {
            throw new BusinessRuleViolationException("No tiene permisos para observar planes de tesis fuera de su grupo de investigación");
        }
        plan.marcarObservado();
        ThesisPlan guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, ThesisProcedureStatus.OBSERVADO,
                ReviewerRole.ESTUDIANTE, "OBSERVAR_COORDINADOR", command.observacion(), command.idDocumentoAdjunto());
        return toResponse(guardado);
    }

    @Override
    public ThesisPlanResponse rechazarPorCoordinador(Integer idPlanTesis, String motivo) {
        ThesisPlan plan = obtenerPlan(idPlanTesis);
        validarRolCoordinador();
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        if (!grupoValidation.esCoordinadorDelGrupo(idUsuarioAccion, plan.getIdGrupo())) {
            throw new BusinessRuleViolationException("No tiene permisos para rechazar planes de tesis fuera de su grupo de investigación");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new BusinessRuleViolationException("El motivo de rechazo es obligatorio");
        }
        plan.marcarRechazado();
        ThesisPlan guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, ThesisProcedureStatus.RECHAZADO,
                ReviewerRole.SIN_REVISOR, "RECHAZAR_COORDINADOR", motivo, null);
        return toResponse(guardado);
    }

    @Override
    public ThesisPlanResponse aprobarPorDirector(Integer idPlanTesis) {
        ThesisPlan plan = obtenerPlan(idPlanTesis);
        validarRolDirector();
        if (plan.getEstadoPlan() != ThesisPlanStatus.APROBADO) {
            throw new BusinessRuleViolationException("Solo se pueden aprobar planes previamente aprobados por el coordinador");
        }
        plan.marcarAprobado();
        ThesisPlan guardado = planRepository.save(plan);
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, ThesisProcedureStatus.PENDIENTE_DECANATO,
                ReviewerRole.DECANO, "APROBAR_DIRECTOR", null, null);
        return toResponse(guardado);
    }

    @Override
    public ThesisPlanResponse observarPorDirector(Integer idPlanTesis, ObserveThesisPlanCommand command) {
        ThesisPlan plan = obtenerPlan(idPlanTesis);
        validarRolDirector();
        plan.marcarObservado();
        ThesisPlan guardado = planRepository.save(plan);
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        // RF-50: si el Director observa, retorna al Coordinador de Grupo, no directamente al estudiante.
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, ThesisProcedureStatus.OBSERVADO,
                ReviewerRole.COORDINADOR_GRUPO, "OBSERVAR_DIRECTOR", command.observacion(), command.idDocumentoAdjunto());
        return toResponse(guardado);
    }

    @Override
    public ThesisPlanResponse subsanarPlan(Integer idPlanTesis, RectifyThesisPlanCommand command) {
        ThesisPlan plan = obtenerPlan(idPlanTesis);
        Long idEstudiante = extraerIdEstudianteDelContexto();
        if (!plan.getIdEstudiante().equals(idEstudiante)) {
            throw new BusinessRuleViolationException("El plan solo puede ser subsanado por el estudiante propietario");
        }
        if (command.idDocumentoActual() != null && !documentoValidation.existeDocumentoActivo(command.idDocumentoActual())) {
            throw new BusinessRuleViolationException("El documento de subsanación no existe o no está activo");
        }
        if (command.idDocumentoActual() == null && (command.resumenSubsanado() == null || command.resumenSubsanado().isBlank())) {
            throw new BusinessRuleViolationException("Debe adjuntar un documento o actualizar el resumen para subsanar");
        }
        plan.subsanar(command.idDocumentoActual(), command.resumenSubsanado());
        ThesisPlan guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idEstudiante, ThesisProcedureStatus.SUBSANADO,
                ReviewerRole.COORDINADOR_GRUPO, "SUBSANAR_PLAN_TESIS", command.comentarioSubsanacion(), command.idDocumentoActual());
        return toResponse(guardado);
    }

    @Override
    public ThesisPlanResponse registrarResolucion(Integer idPlanTesis, RegisterResolutionCommand command) {
        ThesisPlan plan = obtenerPlan(idPlanTesis);
        validarRolDecano();
        if (plan.getEstadoPlan() != ThesisPlanStatus.APROBADO) {
            throw new BusinessRuleViolationException("Solo se puede registrar resolución para planes aprobados");
        }
        String estadoTramite = tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(idPlanTesis);
        if (!"PENDIENTE_DECANATO".equals(estadoTramite)) {
            throw new BusinessRuleViolationException("El trámite debe estar pendiente de resolución del decano");
        }
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        tramiteWorkflow.registrarResolucion(idPlanTesis, idUsuarioAccion,
                command.numeroResolucion(), command.fechaEmision(), command.asunto(),
                command.idDocumentoAdjunto());
        return toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public ThesisPlanResponse obtenerPorId(Integer idPlanTesis) {
        ThesisPlan plan = obtenerPlan(idPlanTesis);
        validarAccesoPlan(plan);
        return toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThesisPlanResponse> listarPorEstudiante(Long idEstudiante) {
        Long idResuelto = resolverIdEstudianteSegunRol(idEstudiante);
        boolean esDecano = esRolDecano();

        List<ThesisPlanResponse> resultados = planRepository.findByEstudiante(idResuelto).stream()
                .map(this::toResponse)
                .toList();

        if (esDecano) {
            return resultados.stream()
                    .filter(p -> p.estadoTramite() != null && p.estadoTramite() == ThesisProcedureStatus.PENDIENTE_DECANATO)
                    .toList();
        }

        return resultados;
    }

    private boolean esRolDecano() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_DECANO));
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThesisPlanResponse> listarPorGrupo(Integer idGrupo) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esCoordinador = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_COORDINADOR_GRUPO));
            boolean esEstudiante = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_ESTUDIANTE));

            if (esEstudiante) {
                throw new BusinessRuleViolationException("Los estudiantes no tienen permisos para listar planes de tesis de un grupo");
            }
            if (esCoordinador && !grupoValidation.esCoordinadorDelGrupo(userDetails.getId(), idGrupo)) {
                throw new BusinessRuleViolationException("No tiene permisos para ver planes de tesis de otro grupo de investigación");
            }
        }
        return planRepository.findByGrupo(idGrupo).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("all")
    public List<ThesisPlanResponse> listarPendientesPorRevisor(ReviewerRole revisor) {
        validarRevisorParaRol(revisor);
        List<Integer> ids = tramiteWorkflow.findPlanTesisIdsByRevisor(revisor);
        return ids.stream()
                .map(planRepository::findById)
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .map(this::toResponse)
                .toList();
    }

    private Long extraerIdUsuarioDelContexto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new BusinessRuleViolationException(MSG_USUARIO_NO_AUTENTICADO);
    }

    private void validarRolCoordinador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esCoordinador = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_COORDINADOR_GRUPO));
            if (!esCoordinador) {
                throw new BusinessRuleViolationException("Solo los coordinadores de grupo pueden realizar esta acción");
            }
            return;
        }
        throw new BusinessRuleViolationException(MSG_USUARIO_NO_AUTENTICADO);
    }

    private void validarRolDirector() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esDirector = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_DIRECTOR_INVESTIGACION));
            if (!esDirector) {
                throw new BusinessRuleViolationException("Solo los directores de investigación pueden realizar esta acción");
            }
            return;
        }
        throw new BusinessRuleViolationException(MSG_USUARIO_NO_AUTENTICADO);
    }

    private void validarRolDecano() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esDecano = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_DECANO));
            if (!esDecano) {
                throw new BusinessRuleViolationException("Solo el decano puede registrar resoluciones");
            }
            return;
        }
        throw new BusinessRuleViolationException(MSG_USUARIO_NO_AUTENTICADO);
    }

    private Long resolverIdEstudianteSegunRol(Long idEstudiante) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esEstudiante = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_ESTUDIANTE));
            if (esEstudiante) {
                return userDetails.getId();
            }
            return idEstudiante;
        }
        throw new BusinessRuleViolationException(MSG_USUARIO_NO_AUTENTICADO);
    }

    private void validarRevisorParaRol(ReviewerRole revisor) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean autorizado = switch (revisor) {
                case ESTUDIANTE -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(ROLE_ESTUDIANTE));
                case COORDINADOR_GRUPO -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(ROLE_COORDINADOR_GRUPO));
                case DIRECTOR_INVESTIGACION -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(ROLE_DIRECTOR_INVESTIGACION));
                case DECANO -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals(ROLE_DECANO));
                case SIN_REVISOR -> false;
            };
            if (!autorizado) {
                throw new BusinessRuleViolationException("No tiene permisos para consultar pendientes del rol " + revisor);
            }
            return;
        }
        throw new BusinessRuleViolationException(MSG_USUARIO_NO_AUTENTICADO);
    }

    private ThesisPlan obtenerPlan(Integer idPlanTesis) {
        return planRepository.findById(idPlanTesis).orElseThrow(() -> new ThesisPlanNotFoundException(idPlanTesis));
    }

    private void validarAccesoPlan(ThesisPlan plan) {
        CustomUserDetails userDetails = getAuthenticatedUser();

        if (hasRole(userDetails, ROLE_ESTUDIANTE)) {
            if (!plan.getIdEstudiante().equals(userDetails.getId())) {
                throw new BusinessRuleViolationException("No tiene permisos para ver planes de tesis de otros estudiantes");
            }
            return;
        }
        if (hasRole(userDetails, ROLE_COORDINADOR_GRUPO) && tieneAccesoCoordinador(userDetails, plan)) {
            return;
        }
        if (hasRole(userDetails, ROLE_DIRECTOR_INVESTIGACION) && tieneAccesoPorEstado(plan, ROLE_DIRECTOR_INVESTIGACION)) {
            return;
        }
        if (hasRole(userDetails, ROLE_DECANO) && tieneAccesoPorEstado(plan, ROLE_DECANO)) {
            return;
        }
        throw new BusinessRuleViolationException("No tiene permisos para acceder a este plan de tesis");
    }

    private CustomUserDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new BusinessRuleViolationException(MSG_USUARIO_NO_AUTENTICADO);
        }
        return userDetails;
    }

    private boolean hasRole(CustomUserDetails user, String role) {
        return user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(role));
    }

    private boolean tieneAccesoCoordinador(CustomUserDetails userDetails, ThesisPlan plan) {
        if (grupoValidation.esCoordinadorDelGrupo(userDetails.getId(), plan.getIdGrupo())) {
            return true;
        }
        String estadoTramite = tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(plan.getIdPlanTesis());
        return estadoTramite != null && "PENDIENTE_COORDINADOR".equals(estadoTramite);
    }

    private boolean tieneAccesoPorEstado(ThesisPlan plan, String rol) {
        String estadoTramite = tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(plan.getIdPlanTesis());
        return switch (rol) {
            case ROLE_DIRECTOR_INVESTIGACION -> estadoTramite != null && "PENDIENTE_DIRECCION".equals(estadoTramite);
            case ROLE_DECANO -> estadoTramite != null && "PENDIENTE_DECANATO".equals(estadoTramite);
            default -> false;
        };
    }

    private void validarGrupoLineaYDocumento(Integer idGrupo, Integer idLinea, Integer idDocumento, Long idUsuario) {
        if (!grupoValidation.existeGrupoActivo(idGrupo)) throw new BusinessRuleViolationException("El grupo de investigación no existe o está inactivo");
        if (!grupoValidation.existeLineaActiva(idLinea)) throw new BusinessRuleViolationException("La línea de investigación no existe o está inactiva");
        if (!grupoValidation.lineaPerteneceAlGrupo(idGrupo, idLinea)) throw new BusinessRuleViolationException("La línea seleccionada no pertenece al grupo de investigación");
        if (idDocumento != null) {
            if (!documentoValidation.existeDocumentoActivo(idDocumento))
                throw new BusinessRuleViolationException("El documento no existe o está inactivo");
            if (!documentoValidation.documentoPerteneceAUsuario(idDocumento, idUsuario))
                throw new BusinessRuleViolationException("El documento no pertenece al estudiante autenticado");
        }
    }

    private ThesisPlanResponse toResponse(ThesisPlan p) {
        try {
            Integer idTramite = tramiteWorkflow.obtenerIdTramitePorPlanTesis(p.getIdPlanTesis());
            String estadoTramite = tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(p.getIdPlanTesis());
            String revisorActual = tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(p.getIdPlanTesis());

            String nombreEstudiante = null;
            String apellidoEstudiante = null;
            try {
                var row = jdbcTemplate.queryForMap("SELECT nombres, apellidos FROM usuarios WHERE id_usuario = ?", p.getIdEstudiante());
                nombreEstudiante = (String) row.get("nombres");
                apellidoEstudiante = (String) row.get("apellidos");
            } catch (Exception e) {
                log.warn("Error resolving student name for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
            }

            String nombreGrupo = null;
            String codigoGrupo = null;
            try {
                var row = jdbcTemplate.queryForMap("SELECT nombre_grupo, codigo_grupo FROM grupos_investigacion WHERE id_grupo = ?", p.getIdGrupo());
                nombreGrupo = (String) row.get("nombre_grupo");
                codigoGrupo = (String) row.get("codigo_grupo");
            } catch (Exception e) {
                log.warn("Error resolving group info for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
            }

            String nombreLinea = null;
            try {
                var row = jdbcTemplate.queryForMap("SELECT nombre_linea FROM lineas_investigacion WHERE id_linea = ?", p.getIdLinea());
                nombreLinea = (String) row.get("nombre_linea");
            } catch (Exception e) {
                log.warn("Error resolving line name for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
            }

            String nombreDocumento = null;
            if (p.getIdDocumentoActual() != null) {
                try {
                    var row = jdbcTemplate.queryForMap("SELECT nombre_original FROM documentos WHERE id_documento = ?", p.getIdDocumentoActual());
                    nombreDocumento = (String) row.get("nombre_original");
                } catch (Exception e) {
                    log.warn("Error resolving document name for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
                }
            }

            return new ThesisPlanResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(),
                    p.getIdEstudiante(), p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), p.getFechaCreacion(), p.getFechaActualizacion(),
                    idTramite, ThesisProcedureStatus.valueOf(estadoTramite), ReviewerRole.valueOf(revisorActual),
                    nombreEstudiante, apellidoEstudiante, nombreGrupo, codigoGrupo, nombreLinea, nombreDocumento);
        } catch (Exception e) {
            log.warn("Error resolving thesis plan details for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
            return new ThesisPlanResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(),
                    p.getIdEstudiante(), p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), p.getFechaCreacion(), p.getFechaActualizacion(),
                    null, null, null, null, null, null, null, null, null);
        }
    }

    private ThesisPlanResponse toResponse(ThesisPlan p, Integer idTramite) {
        try {
            String estadoTramite = tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(p.getIdPlanTesis());
            String revisorActual = tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(p.getIdPlanTesis());

            String nombreEstudiante = null;
            String apellidoEstudiante = null;
            try {
                var row = jdbcTemplate.queryForMap("SELECT nombres, apellidos FROM usuarios WHERE id_usuario = ?", p.getIdEstudiante());
                nombreEstudiante = (String) row.get("nombres");
                apellidoEstudiante = (String) row.get("apellidos");
            } catch (Exception e) {
                log.warn("Error resolving student name for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
            }

            String nombreGrupo = null;
            String codigoGrupo = null;
            try {
                var row = jdbcTemplate.queryForMap("SELECT nombre_grupo, codigo_grupo FROM grupos_investigacion WHERE id_grupo = ?", p.getIdGrupo());
                nombreGrupo = (String) row.get("nombre_grupo");
                codigoGrupo = (String) row.get("codigo_grupo");
            } catch (Exception e) {
                log.warn("Error resolving group info for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
            }

            String nombreLinea = null;
            try {
                var row = jdbcTemplate.queryForMap("SELECT nombre_linea FROM lineas_investigacion WHERE id_linea = ?", p.getIdLinea());
                nombreLinea = (String) row.get("nombre_linea");
            } catch (Exception e) {
                log.warn("Error resolving line name for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
            }

            String nombreDocumento = null;
            if (p.getIdDocumentoActual() != null) {
                try {
                    var row = jdbcTemplate.queryForMap("SELECT nombre_original FROM documentos WHERE id_documento = ?", p.getIdDocumentoActual());
                    nombreDocumento = (String) row.get("nombre_original");
                } catch (Exception e) {
                    log.warn("Error resolving document name for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
                }
            }

            return new ThesisPlanResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(),
                    p.getIdEstudiante(), p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), p.getFechaCreacion(), p.getFechaActualizacion(),
                    idTramite, ThesisProcedureStatus.valueOf(estadoTramite), ReviewerRole.valueOf(revisorActual),
                    nombreEstudiante, apellidoEstudiante, nombreGrupo, codigoGrupo, nombreLinea, nombreDocumento);
        } catch (Exception e) {
            log.warn("Error resolving thesis plan details for plan {}: {}", p.getIdPlanTesis(), e.getMessage());
            return new ThesisPlanResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(),
                    p.getIdEstudiante(), p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), p.getFechaCreacion(), p.getFechaActualizacion(),
                    idTramite, null, null, null, null, null, null, null, null);
        }
    }
}
