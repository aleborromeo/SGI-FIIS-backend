package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.event.ProcedureApprovedEvent;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureEventPublisherPort;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.tramites.domain.service.ProcedureStateMachine;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ApproveProcedureUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;
    private final ProcedureEventPublisherPort eventPublisherPort;
    private final ProcedureStateMachine stateMachine = new ProcedureStateMachine();

    public ApproveProcedureUseCase(ProcedureRepositoryPort procedureRepositoryPort,
                                  ProcedureEventPublisherPort eventPublisherPort) {
        this.procedureRepositoryPort = procedureRepositoryPort;
        this.eventPublisherPort    = eventPublisherPort;
    }

    @Transactional
    public ProcedureResponseDto execute(Long idTramite, RoleEnum rolEjecutor, Long idEjecutor) {
        Procedure tramite = procedureRepositoryPort.findById(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        switch (rolEjecutor) {
            case COORDINADOR_GRUPO      -> stateMachine.aprobarPorCoordinador(tramite, idEjecutor);
            case DIRECTOR_INVESTIGACION -> stateMachine.aprobarPorDirector(tramite, idEjecutor);
            default -> throw new BusinessException(
                    "El rol [" + rolEjecutor + "] no puede aprobar trámites");
        }

        Procedure guardado = procedureRepositoryPort.save(tramite);

        eventPublisherPort.publishProcedureApproved(ProcedureApprovedEvent.builder()
                .procedureId(guardado.getId())
                .code(guardado.getCode())
                .procedureType(guardado.getProcedureType())
                .applicantId(guardado.getApplicantId())
                .resultingStatus(guardado.getCurrentStatus())
                .approverId(idEjecutor)
                .approverRole(rolEjecutor)
                .approvalDate(LocalDateTime.now(ZoneId.systemDefault()))
                .build());

        return ProcedureMapper.toResponse(guardado);
    }
}
