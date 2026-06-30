package pe.unas.fiis.sgifiis.thesis.application.service;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unas.fiis.sgifiis.thesis.application.dto.*;
import pe.unas.fiis.sgifiis.thesis.domain.*;
import pe.unas.fiis.sgifiis.thesis.domain.exception.*;
import pe.unas.fiis.sgifiis.thesis.domain.port.in.InformeTesisUseCase;
import pe.unas.fiis.sgifiis.thesis.domain.port.out.*;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;

@Service
@Transactional
public class InformeTesisService implements InformeTesisUseCase {
    private final InformeTesisRepositoryPort informeRepository;
    private final PlanTesisRepositoryPort planRepository;
    private final DocumentoValidationPort documentoValidation;

    public InformeTesisService(InformeTesisRepositoryPort informeRepository,
                               PlanTesisRepositoryPort planRepository,
                               DocumentoValidationPort documentoValidation) {
        this.informeRepository = informeRepository;
        this.planRepository = planRepository;
        this.documentoValidation = documentoValidation;
    }

    @Override
    public InformeTesisResponse registrarInformeFinal(RegistrarInformeTesisCommand command) {
        Long idEstudiante = extraerIdEstudianteDelContexto();
        PlanTesis plan = planRepository.findById(command.idPlanTesis())
                .orElseThrow(() -> new PlanTesisNoEncontradoException(command.idPlanTesis()));
        if (!plan.getIdEstudiante().equals(idEstudiante)) {
            throw new ReglaDeNegocioVioladaException("El informe final solo puede registrarlo el estudiante propietario del plan");
        }
        if (plan.getEstadoPlan() != EstadoPlanTesis.APROBADO) {
            throw new ReglaDeNegocioVioladaException("Solo se puede registrar informe final de un plan aprobado");
        }
        if (!documentoValidation.existeDocumentoActivo(command.idDocumentoTesis())) {
            throw new ReglaDeNegocioVioladaException("El documento de tesis no existe o no está activo");
        }
        InformeTesis guardado = informeRepository.save(InformeTesis.nuevo(command.idPlanTesis(), command.tituloFinal(), command.idDocumentoTesis()));
        return toResponse(guardado);
    }

    @Override
    public InformeTesisResponse aprobarInforme(Integer idInformeTesis) {
        validarRolDirector();
        InformeTesis informe = obtenerInforme(idInformeTesis);
        informe.aprobar();
        return toResponse(informeRepository.save(informe));
    }

    @Override
    public InformeTesisResponse observarInforme(Integer idInformeTesis, String observacion) {
        validarRolDirector();
        InformeTesis informe = obtenerInforme(idInformeTesis);
        informe.observar();
        return toResponse(informeRepository.save(informe));
    }

    @Override
    @Transactional(readOnly = true)
    public InformeTesisResponse obtenerPorId(Integer idInformeTesis) {
        return toResponse(obtenerInforme(idInformeTesis));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InformeTesisResponse> listarPorPlan(Integer idPlanTesis) {
        return informeRepository.findByPlanTesis(idPlanTesis).stream().map(this::toResponse).toList();
    }

    private Long extraerIdEstudianteDelContexto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esEstudiante = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ESTUDIANTE"));
            if (!esEstudiante) {
                throw new ReglaDeNegocioVioladaException("Solo los estudiantes pueden registrar un informe de tesis");
            }
            return userDetails.getId();
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al estudiante autenticado");
    }

    private void validarRolDirector() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            boolean esDirector = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTOR_INVESTIGACION"));
            if (!esDirector) {
                throw new ReglaDeNegocioVioladaException("Solo los directores de investigación pueden revisar informes de tesis");
            }
            return;
        }
        throw new ReglaDeNegocioVioladaException("No se pudo identificar al usuario autenticado");
    }

    private InformeTesis obtenerInforme(Integer idInformeTesis) {
        return informeRepository.findById(idInformeTesis).orElseThrow(() -> new InformeTesisNoEncontradoException(idInformeTesis));
    }

    private InformeTesisResponse toResponse(InformeTesis i) {
        return new InformeTesisResponse(i.getIdInformeTesis(), i.getIdPlanTesis(), i.getTituloFinal(),
                i.getIdDocumentoTesis(), i.getFechaPresentacion(), i.getEstadoInforme());
    }
}
