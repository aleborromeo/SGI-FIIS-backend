package com.sgi.fiis.tramites.application.usecase;

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

    private final ProcedureRepositoryPort tramiteRepositoryPort;
    private final ProcedureEventPublisherPort eventPublisherPort;
    private final ProcedureStateMachine stateMachine = new ProcedureStateMachine();

    public FlagProcedureUseCase(ProcedureRepositoryPort tramiteRepositoryPort,
                                   ProcedureEventPublisherPort eventPublisherPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
        this.eventPublisherPort    = eventPublisherPort;
    }

    @Transactional
    public ProcedureResponseDto execute(Long idTramite, RoleEnum rolEjecutor, Long idEjecutor,
                                      String textoObservacion) {
        Procedure tramite = tramiteRepositoryPort.findById(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        switch (rolEjecutor) {
            case COORDINADOR_GRUPO      -> stateMachine.observarPorCoordinador(tramite, idEjecutor, textoObservacion);
            case DIRECTOR_INVESTIGACION -> stateMachine.observarPorDirector(tramite, idEjecutor, textoObservacion);
            case DECANO                 -> stateMachine.observarPorDecano(tramite, idEjecutor, textoObservacion);
            default -> throw new BusinessException(
                    "El rol [" + rolEjecutor + "] no puede observar trámites");
        }

        Procedure guardado = tramiteRepositoryPort.save(tramite);

        eventPublisherPort.publishProcedureFlagged(ProcedureFlaggedEvent.builder()
                .idTramite(guardado.getId())
                .codigoTramite(guardado.getCodigoTramite())
                .tipoTramite(guardado.getTipoTramite())
                .idSolicitante(guardado.getIdSolicitante())
                .idObservador(idEjecutor)
                .rolObservador(rolEjecutor)
                .textoObservacion(textoObservacion)
                .fechaObservacion(LocalDateTime.now(ZoneId.systemDefault()))
                .build());

        return ProcedureMapper.toResponse(guardado);
    }
}
