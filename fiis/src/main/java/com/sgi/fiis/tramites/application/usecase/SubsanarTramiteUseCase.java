package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.TramiteResponseDto;
import com.sgi.fiis.tramites.application.mapper.TramiteMapper;
import com.sgi.fiis.tramites.domain.model.Tramite;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import com.sgi.fiis.tramites.domain.service.TramiteStateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubsanarTramiteUseCase {

    private final TramiteRepositoryPort tramiteRepositoryPort;
    private final TramiteStateMachine stateMachine = new TramiteStateMachine();

    public SubsanarTramiteUseCase(TramiteRepositoryPort tramiteRepositoryPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
    }

    @Transactional
    public TramiteResponseDto execute(Long idTramite, Long idSolicitante, String detalleSubsanacion) {
        Tramite tramite = tramiteRepositoryPort.buscarPorId(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        stateMachine.subsanarPorSolicitante(tramite, idSolicitante, detalleSubsanacion);

        return TramiteMapper.toResponse(tramiteRepositoryPort.guardar(tramite));
    }
}
