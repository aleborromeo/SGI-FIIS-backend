package com.sgi.fiis.thesis.application.service;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.*;
import com.sgi.fiis.thesis.domain.exception.*;
import com.sgi.fiis.thesis.domain.port.in.ThesisReportUseCase;
import com.sgi.fiis.thesis.domain.port.out.*;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;

@Service
@Transactional
public class ThesisReportService implements ThesisReportUseCase {
    private static final String ROLE_ESTUDIANTE = "ROLE_ESTUDIANTE";
    private static final String ROLE_DIRECTOR_INVESTIGACION = "ROLE_DIRECTOR_INVESTIGACION";

    private final ThesisReportRepositoryPort informeRepository;
    private final ThesisPlanRepositoryPort planRepository;
    private final DocumentValidationPort documentoValidation;

    public ThesisReportService(ThesisReportRepositoryPort informeRepository,
                               ThesisPlanRepositoryPort planRepository,
                               DocumentValidationPort documentoValidation) {
        this.informeRepository = informeRepository;
        this.planRepository = planRepository;
        this.documentoValidation = documentoValidation;
    }

    @Override
    public ThesisReportResponse registrarInformeFinal(RegisterThesisReportCommand command) {
        Long idEstudiante = extraerIdEstudianteDelContexto();
        ThesisPlan plan = planRepository.findById(command.idPlanTesis())
                .orElseThrow(() -> new ThesisPlanNotFoundException(command.idPlanTesis()));
        if (!plan.getIdEstudiante().equals(idEstudiante)) {
            throw new BusinessRuleViolationException("El informe final solo puede registrarlo el estudiante propietario del plan");
        }
        if (plan.getEstadoPlan() != ThesisPlanStatus.APROBADO) {
            throw new BusinessRuleViolationException("Solo se puede registrar informe final de un plan aprobado");
        }
        if (!documentoValidation.existeDocumentoActivo(command.idDocumentoTesis())) {
            throw new BusinessRuleViolationException("El documento de tesis no existe o no está activo");
        }
        ThesisReport guardado = informeRepository.save(ThesisReport.nuevo(command.idPlanTesis(), command.tituloFinal(), command.idDocumentoTesis()));
        return toResponse(guardado);
    }

    @Override
    public ThesisReportResponse aprobarInforme(Integer idInformeTesis) {
        validarRolDirector();
        ThesisReport informe = obtenerInforme(idInformeTesis);
        informe.aprobar();
        return toResponse(informeRepository.save(informe));
    }

    @Override
    public ThesisReportResponse observarInforme(Integer idInformeTesis, String observacion) {
        validarRolDirector();
        ThesisReport informe = obtenerInforme(idInformeTesis);
        informe.observar();
        return toResponse(informeRepository.save(informe));
    }

    @Override
    @Transactional(readOnly = true)
    public ThesisReportResponse obtenerPorId(Integer idInformeTesis) {
        return toResponse(obtenerInforme(idInformeTesis));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThesisReportResponse> listarPorPlan(Integer idPlanTesis) {
        return informeRepository.findByPlanTesis(idPlanTesis).stream().map(this::toResponse).toList();
    }

    private Long extraerIdEstudianteDelContexto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esEstudiante = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_ESTUDIANTE));
            if (!esEstudiante) {
                throw new BusinessRuleViolationException("Solo los estudiantes pueden registrar un informe de tesis");
            }
            return userDetails.getId();
        }
        throw new BusinessRuleViolationException("No se pudo identificar al estudiante autenticado");
    }

    private void validarRolDirector() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esDirector = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_DIRECTOR_INVESTIGACION));
            if (!esDirector) {
                throw new BusinessRuleViolationException("Solo los directores de investigación pueden revisar informes de tesis");
            }
            return;
        }
        throw new BusinessRuleViolationException("No se pudo identificar al usuario autenticado");
    }

    private ThesisReport obtenerInforme(Integer idInformeTesis) {
        return informeRepository.findById(idInformeTesis).orElseThrow(() -> new ThesisReportNotFoundException(idInformeTesis));
    }

    private ThesisReportResponse toResponse(ThesisReport i) {
        return new ThesisReportResponse(i.getIdInformeTesis(), i.getIdPlanTesis(), i.getTituloFinal(),
                i.getIdDocumentoTesis(), i.getFechaPresentacion(), i.getEstadoInforme());
    }
}
