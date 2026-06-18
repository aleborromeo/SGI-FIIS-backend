package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.application.mapper.TramiteMapper;
import com.sgi.fiis.tramites.domain.event.TramiteFinalizadoEvent;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.port.TramiteEventPublisherPort;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import com.sgi.fiis.tramites.domain.service.TramiteStateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrarResolucionUseCase {

    private final TramiteRepositoryPort tramiteRepositoryPort;
    private final TramiteEventPublisherPort eventPublisherPort;
    private final TramiteStateMachine stateMachine = new TramiteStateMachine();

    public RegistrarResolucionUseCase(TramiteRepositoryPort tramiteRepositoryPort,
                                       TramiteEventPublisherPort eventPublisherPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
        this.eventPublisherPort    = eventPublisherPort;
    }

    @Transactional
    public TramiteResponseDto execute(Long idTramite, Long idDecano) {
        Tramite tramite = tramiteRepositoryPort.buscarPorId(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        stateMachine.registrarResolucionPorDecano(tramite, idDecano);

        Tramite guardado = tramiteRepositoryPort.guardar(tramite);

        eventPublisherPort.publicarTramiteFinalizado(TramiteFinalizadoEvent.builder()
                .idTramite(guardado.getId())
                .codigoTramite(guardado.getCodigoTramite())
                .tipoTramite(guardado.getTipoTramite())
                .idSolicitante(guardado.getIdSolicitante())
                .fechaFinalizacion(LocalDateTime.now())
                .build());

        return TramiteMapper.toResponse(guardado);
    }
}
