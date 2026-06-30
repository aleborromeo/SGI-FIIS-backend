package pe.unas.fiis.sgifiis.thesis.application.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unas.fiis.sgifiis.thesis.application.dto.*;
import pe.unas.fiis.sgifiis.thesis.domain.*;
import pe.unas.fiis.sgifiis.thesis.domain.exception.*;
import pe.unas.fiis.sgifiis.thesis.domain.port.in.PlanTesisUseCase;
import pe.unas.fiis.sgifiis.thesis.domain.port.out.*;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;

@Service
@Transactional
public class PlanTesisService implements PlanTesisUseCase {
    private final PlanTesisRepositoryPort planRepository;
    private final TramiteWorkflowPort tramiteWorkflow;
    private final DocumentoValidationPort documentoValidation;
    private final GrupoInvestigacionValidationPort grupoValidation;

    public PlanTesisService(PlanTesisRepositoryPort planRepository,
                            TramiteWorkflowPort tramiteWorkflow,
                            DocumentoValidationPort documentoValidation,
                            GrupoInvestigacionValidationPort grupoValidation) {
        this.planRepository = planRepository;
        this.tramiteWorkflow = tramiteWorkflow;
        this.documentoValidation = documentoValidation;
        this.grupoValidation = grupoValidation;
    }

    @Override
    public PlanTesisResponse registrarPlan(RegistrarPlanTesisCommand command) {
        Long idEstudiante = extraerIdEstudianteDelContexto();
        validarGrupoLineaYDocumento(command.idGrupo(), command.idLinea(), command.idDocumentoActual(), idEstudiante);
        PlanTesis guardado = planRepository.save(PlanTesis.nuevo(
                command.tituloTesis(), command.resumen(), idEstudiante, command.idLinea(),
                command.idGrupo(), command.idDocumentoActual()));
        Integer idTramite = tramiteWorkflow.crearTramitePlanTesis(guardado.getIdPlanTesis(), guardado.getIdEstudiante(), guardado.getIdGrupo());
        return toResponse(guardado, idTramite);
    }

    private Long extraerIdEstudianteDelContexto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esEstudiante = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
            if (!esEstudiante) {
                throw new ReglaDeNegocioVioladaException("Solo los estudiantes pueden registrar un plan de tesis");
            }
            return userDetails.getId();
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al estudiante autenticado");
    }

    @Override
    public PlanTesisResponse aprobarPorCoordinador(Integer idPlanTesis) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        validarRolCoordinador();
        if (plan.getEstadoPlan() != EstadoPlanTesis.POSTULADO) {
            throw new ReglaDeNegocioVioladaException("Solo se pueden aprobar planes en estado POSTULADO");
        }
        plan.marcarAprobado();
        PlanTesis guardado = planRepository.save(plan);
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, EstadoTramiteTesis.PENDIENTE_DIRECCION,
                RolRevisor.DIRECTOR_INVESTIGACION, "APROBAR_COORDINADOR", null, null);
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse observarPorCoordinador(Integer idPlanTesis, ObservarPlanTesisCommand command) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        validarRolCoordinador();
        plan.marcarObservado();
        PlanTesis guardado = planRepository.save(plan);
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, EstadoTramiteTesis.OBSERVADO,
                RolRevisor.ESTUDIANTE, "OBSERVAR_COORDINADOR", command.observacion(), command.idDocumentoAdjunto());
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse rechazarPorCoordinador(Integer idPlanTesis, String motivo) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        validarRolCoordinador();
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaDeNegocioVioladaException("El motivo de rechazo es obligatorio");
        }
        plan.marcarRechazado();
        PlanTesis guardado = planRepository.save(plan);
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, EstadoTramiteTesis.RECHAZADO,
                RolRevisor.SIN_REVISOR, "RECHAZAR_COORDINADOR", motivo, null);
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse aprobarPorDirector(Integer idPlanTesis) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        validarRolDirector();
        if (plan.getEstadoPlan() != EstadoPlanTesis.APROBADO) {
            throw new ReglaDeNegocioVioladaException("Solo se pueden aprobar planes previamente aprobados por el coordinador");
        }
        plan.marcarAprobado();
        PlanTesis guardado = planRepository.save(plan);
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, EstadoTramiteTesis.PENDIENTE_DECANATO,
                RolRevisor.DECANO, "APROBAR_DIRECTOR", null, null);
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse observarPorDirector(Integer idPlanTesis, ObservarPlanTesisCommand command) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        validarRolDirector();
        plan.marcarObservado();
        PlanTesis guardado = planRepository.save(plan);
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        // RF-50: si el Director observa, retorna al Coordinador de Grupo, no directamente al estudiante.
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, EstadoTramiteTesis.OBSERVADO,
                RolRevisor.COORDINADOR_GRUPO, "OBSERVAR_DIRECTOR", command.observacion(), command.idDocumentoAdjunto());
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse subsanarPlan(Integer idPlanTesis, SubsanarPlanTesisCommand command) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        Long idEstudiante = extraerIdEstudianteDelContexto();
        if (!plan.getIdEstudiante().equals(idEstudiante)) {
            throw new ReglaDeNegocioVioladaException("El plan solo puede ser subsanado por el estudiante propietario");
        }
        if (command.idDocumentoActual() != null && !documentoValidation.existeDocumentoActivo(command.idDocumentoActual())) {
            throw new ReglaDeNegocioVioladaException("El documento de subsanación no existe o no está activo");
        }
        if (command.idDocumentoActual() == null && (command.resumenSubsanado() == null || command.resumenSubsanado().isBlank())) {
            throw new ReglaDeNegocioVioladaException("Debe adjuntar un documento o actualizar el resumen para subsanar");
        }
        plan.subsanar(command.idDocumentoActual(), command.resumenSubsanado());
        PlanTesis guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idEstudiante, EstadoTramiteTesis.SUBSANADO,
                RolRevisor.COORDINADOR_GRUPO, "SUBSANAR_PLAN_TESIS", command.comentarioSubsanacion(), command.idDocumentoActual());
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse registrarResolucion(Integer idPlanTesis, RegistrarResolucionCommand command) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        validarRolDecano();
        if (plan.getEstadoPlan() != EstadoPlanTesis.APROBADO) {
            throw new ReglaDeNegocioVioladaException("Solo se puede registrar resolución para planes aprobados");
        }
        String estadoTramite = tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(idPlanTesis);
        if (!"PENDIENTE_DECANATO".equals(estadoTramite)) {
            throw new ReglaDeNegocioVioladaException("El trámite debe estar pendiente de resolución del decano");
        }
        Long idUsuarioAccion = extraerIdUsuarioDelContexto();
        tramiteWorkflow.registrarResolucion(idPlanTesis, idUsuarioAccion,
                command.numeroResolucion(), command.fechaEmision(), command.asunto(),
                command.idDocumentoAdjunto());
        return toResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanTesisResponse obtenerPorId(Integer idPlanTesis) {
        return toResponse(obtenerPlan(idPlanTesis));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanTesisResponse> listarPorEstudiante(Long idEstudiante) {
        Long idResuelto = resolverIdEstudianteSegunRol(idEstudiante);
        return planRepository.findByEstudiante(idResuelto).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanTesisResponse> listarPorGrupo(Integer idGrupo) {
        return planRepository.findByGrupo(idGrupo).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanTesisResponse> listarPendientesPorRevisor(RolRevisor revisor) {
        validarRevisorParaRol(revisor);
        List<Integer> ids = tramiteWorkflow.findPlanTesisIdsByRevisor(revisor);
        return ids.stream()
                .map(planRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::toResponse)
                .toList();
    }

    private Long extraerIdUsuarioDelContexto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al usuario autenticado");
    }

    private void validarRolCoordinador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esCoordinador = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_COORDINADOR_GRUPO"));
            if (!esCoordinador) {
                throw new ReglaDeNegocioVioladaException("Solo los coordinadores de grupo pueden realizar esta acción");
            }
            return;
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al usuario autenticado");
    }

    private void validarRolDirector() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esDirector = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTOR_INVESTIGACION"));
            if (!esDirector) {
                throw new ReglaDeNegocioVioladaException("Solo los directores de investigación pueden realizar esta acción");
            }
            return;
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al usuario autenticado");
    }

    private void validarRolDecano() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esDecano = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DECANO"));
            if (!esDecano) {
                throw new ReglaDeNegocioVioladaException("Solo el decano puede registrar resoluciones");
            }
            return;
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al usuario autenticado");
    }

    private Long resolverIdEstudianteSegunRol(Long idEstudiante) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esEstudiante = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
            if (esEstudiante) {
                return userDetails.getId();
            }
            return idEstudiante;
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al usuario autenticado");
    }

    private void validarRevisorParaRol(RolRevisor revisor) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean autorizado = switch (revisor) {
                case ESTUDIANTE -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
                case COORDINADOR_GRUPO -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_COORDINADOR_GRUPO"));
                case DIRECTOR_INVESTIGACION -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTOR_INVESTIGACION"));
                case DECANO -> userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_DECANO"));
                case SIN_REVISOR -> false;
            };
            if (!autorizado) {
                throw new ReglaDeNegocioVioladaException("No tiene permisos para consultar pendientes del rol " + revisor);
            }
            return;
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al usuario autenticado");
    }

    private PlanTesis obtenerPlan(Integer idPlanTesis) {
        return planRepository.findById(idPlanTesis).orElseThrow(() -> new PlanTesisNoEncontradoException(idPlanTesis));
    }

    private void validarGrupoLineaYDocumento(Integer idGrupo, Integer idLinea, Integer idDocumento, Long idUsuario) {
        if (!grupoValidation.existeGrupoActivo(idGrupo)) throw new ReglaDeNegocioVioladaException("El grupo de investigación no existe o está inactivo");
        if (!grupoValidation.existeLineaActiva(idLinea)) throw new ReglaDeNegocioVioladaException("La línea de investigación no existe o está inactiva");
        if (!grupoValidation.lineaPerteneceAlGrupo(idGrupo, idLinea)) throw new ReglaDeNegocioVioladaException("La línea seleccionada no pertenece al grupo de investigación");
        if (idDocumento != null) {
            if (!documentoValidation.existeDocumentoActivo(idDocumento))
                throw new ReglaDeNegocioVioladaException("El documento no existe o está inactivo");
            if (!documentoValidation.documentoPerteneceAUsuario(idDocumento, idUsuario))
                throw new ReglaDeNegocioVioladaException("El documento no pertenece al estudiante autenticado");
        }
    }

    private PlanTesisResponse toResponse(PlanTesis p) {
        try {
            Integer idTramite = tramiteWorkflow.obtenerIdTramitePorPlanTesis(p.getIdPlanTesis());
            String estadoTramite = tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(p.getIdPlanTesis());
            String revisorActual = tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(p.getIdPlanTesis());
            return new PlanTesisResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(),
                    p.getIdEstudiante(), p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), p.getFechaCreacion(), p.getFechaActualizacion(),
                    idTramite, EstadoTramiteTesis.valueOf(estadoTramite), RolRevisor.valueOf(revisorActual));
        } catch (Exception e) {
            return new PlanTesisResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(),
                    p.getIdEstudiante(), p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), p.getFechaCreacion(), p.getFechaActualizacion(),
                    null, null, null);
        }
    }

    private PlanTesisResponse toResponse(PlanTesis p, Integer idTramite) {
        try {
            String estadoTramite = tramiteWorkflow.obtenerEstadoTramitePorPlanTesis(p.getIdPlanTesis());
            String revisorActual = tramiteWorkflow.obtenerRevisorTramitePorPlanTesis(p.getIdPlanTesis());
            return new PlanTesisResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(),
                    p.getIdEstudiante(), p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), p.getFechaCreacion(), p.getFechaActualizacion(),
                    idTramite, EstadoTramiteTesis.valueOf(estadoTramite), RolRevisor.valueOf(revisorActual));
        } catch (Exception e) {
            return new PlanTesisResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(),
                    p.getIdEstudiante(), p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(),
                    p.getEstadoPlan(), p.getFechaCreacion(), p.getFechaActualizacion(),
                    idTramite, null, null);
        }
    }
}
