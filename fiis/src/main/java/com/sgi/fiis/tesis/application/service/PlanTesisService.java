package pe.unas.fiis.sgifiis.thesis.application.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unas.fiis.sgifiis.thesis.application.dto.*;
import pe.unas.fiis.sgifiis.thesis.domain.*;
import pe.unas.fiis.sgifiis.thesis.domain.exception.*;
import pe.unas.fiis.sgifiis.thesis.domain.port.in.PlanTesisUseCase;
import pe.unas.fiis.sgifiis.thesis.domain.port.out.*;

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
        validarGrupoLineaYDocumento(command.idGrupo(), command.idLinea(), command.idDocumentoActual(), command.idEstudiante());
        PlanTesis guardado = planRepository.save(PlanTesis.nuevo(
                command.tituloTesis(), command.resumen(), command.idEstudiante(), command.idLinea(),
                command.idGrupo(), command.idDocumentoActual()));
        tramiteWorkflow.crearTramitePlanTesis(guardado.getIdPlanTesis(), guardado.getIdEstudiante(), guardado.getIdGrupo());
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse aprobarPorCoordinador(Integer idPlanTesis, Integer idUsuarioAccion) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, EstadoTramiteTesis.PENDIENTE_DIRECCION,
                RolRevisor.DIRECTOR_INVESTIGACION, "APROBAR_COORDINADOR", null, null);
        return toResponse(plan);
    }

    @Override
    public PlanTesisResponse observarPorCoordinador(Integer idPlanTesis, ObservarPlanTesisCommand command) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        plan.marcarObservado();
        PlanTesis guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, command.idUsuarioAccion(), EstadoTramiteTesis.OBSERVADO,
                RolRevisor.ESTUDIANTE, "OBSERVAR_COORDINADOR", command.observacion(), command.idDocumentoAdjunto());
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse rechazarPorCoordinador(Integer idPlanTesis, Integer idUsuarioAccion, String motivo) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        plan.marcarRechazado();
        PlanTesis guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, EstadoTramiteTesis.RECHAZADO,
                RolRevisor.SIN_REVISOR, "RECHAZAR_COORDINADOR", motivo, null);
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse aprobarPorDirector(Integer idPlanTesis, Integer idUsuarioAccion) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        plan.marcarAprobado();
        PlanTesis guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, idUsuarioAccion, EstadoTramiteTesis.PENDIENTE_DECANATO,
                RolRevisor.DECANO, "APROBAR_DIRECTOR", null, null);
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse observarPorDirector(Integer idPlanTesis, ObservarPlanTesisCommand command) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        plan.marcarObservado();
        PlanTesis guardado = planRepository.save(plan);
        // RF-50: si el Director observa, retorna al Coordinador de Grupo, no directamente al estudiante.
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, command.idUsuarioAccion(), EstadoTramiteTesis.OBSERVADO,
                RolRevisor.COORDINADOR_GRUPO, "OBSERVAR_DIRECTOR", command.observacion(), command.idDocumentoAdjunto());
        return toResponse(guardado);
    }

    @Override
    public PlanTesisResponse subsanarPlan(Integer idPlanTesis, SubsanarPlanTesisCommand command) {
        PlanTesis plan = obtenerPlan(idPlanTesis);
        if (!plan.getIdEstudiante().equals(command.idEstudiante())) {
            throw new ReglaDeNegocioVioladaException("El plan solo puede ser subsanado por el estudiante propietario");
        }
        if (command.idDocumentoActual() != null && !documentoValidation.existeDocumentoActivo(command.idDocumentoActual())) {
            throw new ReglaDeNegocioVioladaException("El documento de subsanación no existe o no está activo");
        }
        plan.subsanar(command.idDocumentoActual(), command.resumenSubsanado());
        PlanTesis guardado = planRepository.save(plan);
        tramiteWorkflow.derivarPlanTesis(idPlanTesis, command.idEstudiante(), EstadoTramiteTesis.SUBSANADO,
                RolRevisor.COORDINADOR_GRUPO, "SUBSANAR_PLAN_TESIS", command.comentarioSubsanacion(), command.idDocumentoActual());
        return toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanTesisResponse obtenerPorId(Integer idPlanTesis) {
        return toResponse(obtenerPlan(idPlanTesis));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanTesisResponse> listarPorEstudiante(Integer idEstudiante) {
        return planRepository.findByEstudiante(idEstudiante).stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanTesisResponse> listarPorGrupo(Integer idGrupo) {
        return planRepository.findByGrupo(idGrupo).stream().map(this::toResponse).toList();
    }

    private PlanTesis obtenerPlan(Integer idPlanTesis) {
        return planRepository.findById(idPlanTesis).orElseThrow(() -> new PlanTesisNoEncontradoException(idPlanTesis));
    }

    private void validarGrupoLineaYDocumento(Integer idGrupo, Integer idLinea, Integer idDocumento, Integer idUsuario) {
        if (!grupoValidation.existeGrupoActivo(idGrupo)) throw new ReglaDeNegocioVioladaException("El grupo de investigación no existe o está inactivo");
        if (!grupoValidation.existeLineaActiva(idLinea)) throw new ReglaDeNegocioVioladaException("La línea de investigación no existe o está inactiva");
        if (!grupoValidation.lineaPerteneceAlGrupo(idGrupo, idLinea)) throw new ReglaDeNegocioVioladaException("La línea seleccionada no pertenece al grupo de investigación");
        if (idDocumento != null && !documentoValidation.existeDocumentoActivo(idDocumento)) throw new ReglaDeNegocioVioladaException("El documento no existe o está inactivo");
    }

    private PlanTesisResponse toResponse(PlanTesis p) {
        return new PlanTesisResponse(p.getIdPlanTesis(), p.getTituloTesis(), p.getResumen(), p.getIdEstudiante(),
                p.getIdLinea(), p.getIdGrupo(), p.getIdDocumentoActual(), p.getEstadoPlan(),
                p.getFechaCreacion(), p.getFechaActualizacion());
    }
}
