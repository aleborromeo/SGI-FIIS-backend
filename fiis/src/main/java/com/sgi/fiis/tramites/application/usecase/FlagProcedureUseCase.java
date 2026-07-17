package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.observations.application.dto.ObservationRequestDTO;
import com.sgi.fiis.observations.application.usecase.RegisterObservationUseCase;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.event.ProcedureFlaggedEvent;
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
public class FlagProcedureUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;
    private final ProcedureEventPublisherPort eventPublisherPort;
    private final RegisterObservationUseCase registerObservationUseCase;
    private final ProcedureStateMachine stateMachine = new ProcedureStateMachine();

    public FlagProcedureUseCase(ProcedureRepositoryPort procedureRepositoryPort,
                                   ProcedureEventPublisherPort eventPublisherPort,
                                   RegisterObservationUseCase registerObservationUseCase) {
        this.procedureRepositoryPort = procedureRepositoryPort;
        this.eventPublisherPort    = eventPublisherPort;
        this.registerObservationUseCase = registerObservationUseCase;
    }

    @Transactional
    public ProcedureResponseDto execute(Long idTramite, RoleEnum rolEjecutor, Long idEjecutor,
                                      String textoObservacion) {
        Procedure tramite = procedureRepositoryPort.findById(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        switch (rolEjecutor) {
            case COORDINADOR_GRUPO      -> stateMachine.observarPorCoordinador(tramite, idEjecutor, textoObservacion);
            case DIRECTOR_INVESTIGACION -> stateMachine.observarPorDirector(tramite, idEjecutor, textoObservacion);
            case DECANO                 -> stateMachine.observarPorDecano(tramite, idEjecutor, textoObservacion);
            default -> throw new BusinessException(
                    "El rol [" + rolEjecutor + "] no puede observar trámites");
        }

        Procedure guardado = procedureRepositoryPort.save(tramite);

        registerObservationUseCase.execute(ObservationRequestDTO.builder()
                .procedureId(Math.toIntExact(guardado.getId()))
                .reviewerId(Math.toIntExact(idEjecutor))
                .type("TECNICA")
                .description(textoObservacion)
                .reviewerRole(rolEjecutor.name())
                .build());

        eventPublisherPort.publishProcedureFlagged(ProcedureFlaggedEvent.builder()
                .procedureId(guardado.getId())
                .code(guardado.getCode())
                .procedureType(guardado.getProcedureType())
                .applicantId(guardado.getApplicantId())
                .observerId(idEjecutor)
                .observerRole(rolEjecutor)
                .observationText(textoObservacion)
                .observationDate(LocalDateTime.now(ZoneId.systemDefault()))
                .build());

        return ProcedureMapper.toResponse(guardado);
    }
}
