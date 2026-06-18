package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.application.mapper.TramiteMapper;
import com.sgi.fiis.tramites.domain.event.TramiteAprobadoEvent;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.port.TramiteEventPublisherPort;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import com.sgi.fiis.tramites.domain.service.TramiteStateMachine;
import com.sgi.fiis.users.domain.model.RolEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AprobarTramiteUseCase {

    private final TramiteRepositoryPort tramiteRepositoryPort;
    private final TramiteEventPublisherPort eventPublisherPort;
    private final TramiteStateMachine stateMachine = new TramiteStateMachine();

    public AprobarTramiteUseCase(TramiteRepositoryPort tramiteRepositoryPort,
                                  TramiteEventPublisherPort eventPublisherPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
        this.eventPublisherPort    = eventPublisherPort;
    }

    @Transactional
    public TramiteResponseDto execute(Long idTramite, RolEnum rolEjecutor, Long idEjecutor) {
        Tramite tramite = tramiteRepositoryPort.buscarPorId(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        switch (rolEjecutor) {
            case COORDINADOR_GRUPO      -> stateMachine.aprobarPorCoordinador(tramite, idEjecutor);
            case DIRECTOR_INVESTIGACION -> stateMachine.aprobarPorDirector(tramite, idEjecutor);
            default -> throw new BusinessException(
                    "El rol [" + rolEjecutor + "] no puede aprobar trámites");
        }

        Tramite guardado = tramiteRepositoryPort.guardar(tramite);

        eventPublisherPort.publicarTramiteAprobado(TramiteAprobadoEvent.builder()
                .idTramite(guardado.getId())
                .codigoTramite(guardado.getCodigoTramite())
                .tipoTramite(guardado.getTipoTramite())
                .idSolicitante(guardado.getIdSolicitante())
                .estadoResultante(guardado.getEstadoActual())
                .idAprobador(idEjecutor)
                .rolAprobador(rolEjecutor)
                .fechaAprobacion(LocalDateTime.now())
                .build());

        return TramiteMapper.toResponse(guardado);
    }
}
