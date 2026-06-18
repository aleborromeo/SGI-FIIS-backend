package pe.unas.fiis.sgifiis.thesis.application.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unas.fiis.sgifiis.thesis.application.dto.*;
import pe.unas.fiis.sgifiis.thesis.domain.*;
import pe.unas.fiis.sgifiis.thesis.domain.exception.*;
import pe.unas.fiis.sgifiis.thesis.domain.port.in.InformeTesisUseCase;
import pe.unas.fiis.sgifiis.thesis.domain.port.out.*;

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
        PlanTesis plan = planRepository.findById(command.idPlanTesis())
                .orElseThrow(() -> new PlanTesisNoEncontradoException(command.idPlanTesis()));
        if (!plan.getIdEstudiante().equals(command.idEstudiante())) {
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
    public InformeTesisResponse aprobarInforme(Integer idInformeTesis, Integer idUsuarioAccion) {
        InformeTesis informe = obtenerInforme(idInformeTesis);
        informe.aprobar();
        return toResponse(informeRepository.save(informe));
    }

    @Override
    public InformeTesisResponse observarInforme(Integer idInformeTesis, Integer idUsuarioAccion, String observacion) {
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

    private InformeTesis obtenerInforme(Integer idInformeTesis) {
        return informeRepository.findById(idInformeTesis).orElseThrow(() -> new InformeTesisNoEncontradoException(idInformeTesis));
    }

    private InformeTesisResponse toResponse(InformeTesis i) {
        return new InformeTesisResponse(i.getIdInformeTesis(), i.getIdPlanTesis(), i.getTituloFinal(),
                i.getIdDocumentoTesis(), i.getFechaPresentacion(), i.getEstadoInforme());
    }
}
