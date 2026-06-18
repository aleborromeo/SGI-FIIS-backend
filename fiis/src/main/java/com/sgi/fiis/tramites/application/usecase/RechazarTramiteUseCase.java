package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.application.mapper.TramiteMapper;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import com.sgi.fiis.tramites.domain.service.TramiteStateMachine;
import com.sgi.fiis.users.domain.model.RolEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RechazarTramiteUseCase {

    private final TramiteRepositoryPort tramiteRepositoryPort;
    private final TramiteStateMachine stateMachine = new TramiteStateMachine();

    public RechazarTramiteUseCase(TramiteRepositoryPort tramiteRepositoryPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
    }

    @Transactional
    public TramiteResponseDto execute(Long idTramite, RolEnum rolEjecutor, Long idEjecutor) {
        Tramite tramite = tramiteRepositoryPort.buscarPorId(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        switch (rolEjecutor) {
            case COORDINADOR_GRUPO      -> stateMachine.rechazarPorCoordinador(tramite, idEjecutor);
            case DIRECTOR_INVESTIGACION -> stateMachine.rechazarPorDirector(tramite, idEjecutor);
            default -> throw new BusinessException(
                    "El rol [" + rolEjecutor + "] no puede rechazar trámites");
        }

        return TramiteMapper.toResponse(tramiteRepositoryPort.guardar(tramite));
    }
}
