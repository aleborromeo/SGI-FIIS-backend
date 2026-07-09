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
            throw new BusinessRuleViolationException("thesis.error.only-student-owner-report");
        }
        if (plan.getEstadoPlan() != ThesisPlanStatus.APROBADO) {
            throw new BusinessRuleViolationException("thesis.error.only-approved-plan-report");
        }
        if (!documentoValidation.existeDocumentoActivo(command.idDocumentoTesis())) {
            throw new BusinessRuleViolationException("thesis.error.document-inactive-or-null");
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
                throw new BusinessRuleViolationException("thesis.error.only-students-report");
            }
            return userDetails.getId();
        }
        throw new BusinessRuleViolationException("thesis.error.student-not-identified");
    }

    private void validarRolDirector() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esDirector = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(ROLE_DIRECTOR_INVESTIGACION));
            if (!esDirector) {
                throw new BusinessRuleViolationException("thesis.error.only-directors-report-review");
            }
            return;
        }
        throw new BusinessRuleViolationException("thesis.error.user-not-identified");
    }

    private ThesisReport obtenerInforme(Integer idInformeTesis) {
        return informeRepository.findById(idInformeTesis).orElseThrow(() -> new ThesisReportNotFoundException(idInformeTesis));
    }

    private ThesisReportResponse toResponse(ThesisReport i) {
        return new ThesisReportResponse(i.getIdInformeTesis(), i.getIdPlanTesis(), i.getTituloFinal(),
                i.getIdDocumentoTesis(), i.getFechaPresentacion(), i.getEstadoInforme());
    }
}
