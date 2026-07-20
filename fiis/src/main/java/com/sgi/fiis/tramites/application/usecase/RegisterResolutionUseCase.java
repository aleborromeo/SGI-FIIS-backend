package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.event.ProcedureFinalizedEvent;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureEventPublisherPort;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.tramites.domain.service.ProcedureStateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class RegisterResolutionUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;
    private final ProcedureEventPublisherPort eventPublisherPort;
    private final ProcedureStateMachine stateMachine = new ProcedureStateMachine();

    public RegisterResolutionUseCase(ProcedureRepositoryPort procedureRepositoryPort,
                                       ProcedureEventPublisherPort eventPublisherPort) {
        this.procedureRepositoryPort = procedureRepositoryPort;
        this.eventPublisherPort    = eventPublisherPort;
    }

    @Transactional
    @Auditable(action = "REGISTER_RESOLUTION", table = "tramites")
    public ProcedureResponseDto execute(Long idTramite, Long idDecano) {
        Procedure tramite = procedureRepositoryPort.findById(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        stateMachine.registrarResolucionPorDecano(tramite, idDecano);

        Procedure guardado = procedureRepositoryPort.save(tramite);

        eventPublisherPort.publishProcedureFinalized(ProcedureFinalizedEvent.builder()
                .procedureId(guardado.getId())
                .code(guardado.getCode())
                .procedureType(guardado.getProcedureType())
                .applicantId(guardado.getApplicantId())
                .finalizationDate(LocalDateTime.now(ZoneId.systemDefault()))
                .build());

        return ProcedureMapper.toResponse(guardado);
    }
}
