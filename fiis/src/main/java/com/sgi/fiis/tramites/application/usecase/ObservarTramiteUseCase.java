package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.application.mapper.TramiteMapper;
import com.sgi.fiis.tramites.domain.event.TramiteObservadoEvent;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.port.TramiteEventPublisherPort;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import com.sgi.fiis.tramites.domain.service.TramiteStateMachine;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ObservarTramiteUseCase {

    private final TramiteRepositoryPort tramiteRepositoryPort;
    private final TramiteEventPublisherPort eventPublisherPort;
    private final TramiteStateMachine stateMachine = new TramiteStateMachine();

    public ObservarTramiteUseCase(TramiteRepositoryPort tramiteRepositoryPort,
                                   TramiteEventPublisherPort eventPublisherPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
        this.eventPublisherPort    = eventPublisherPort;
    }

    @Transactional
    public TramiteResponseDto execute(Long idTramite, RoleEnum rolEjecutor, Long idEjecutor,
                                      String textoObservacion) {
        Tramite tramite = tramiteRepositoryPort.buscarPorId(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        switch (rolEjecutor) {
            case COORDINADOR_GRUPO      -> stateMachine.observarPorCoordinador(tramite, idEjecutor, textoObservacion);
            case DIRECTOR_INVESTIGACION -> stateMachine.observarPorDirector(tramite, idEjecutor, textoObservacion);
            case DECANO                 -> stateMachine.observarPorDecano(tramite, idEjecutor, textoObservacion);
            default -> throw new BusinessException(
                    "El rol [" + rolEjecutor + "] no puede observar trámites");
        }

        Tramite guardado = tramiteRepositoryPort.guardar(tramite);

        eventPublisherPort.publicarTramiteObservado(TramiteObservadoEvent.builder()
                .idTramite(guardado.getId())
                .codigoTramite(guardado.getCodigoTramite())
                .tipoTramite(guardado.getTipoTramite())
                .idSolicitante(guardado.getIdSolicitante())
                .idObservador(idEjecutor)
                .rolObservador(rolEjecutor)
                .textoObservacion(textoObservacion)
                .fechaObservacion(LocalDateTime.now())
                .build());

        return TramiteMapper.toResponse(guardado);
    }
}
