package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.tramites.domain.service.ProcedureStateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemediateProcedureUseCase {

    private final ProcedureRepositoryPort tramiteRepositoryPort;
    private final ProcedureStateMachine stateMachine = new ProcedureStateMachine();

    public RemediateProcedureUseCase(ProcedureRepositoryPort tramiteRepositoryPort) {
        this.tramiteRepositoryPort = tramiteRepositoryPort;
    }

    @Transactional
    public ProcedureResponseDto execute(Long idTramite, Long idSolicitante, String detalleSubsanacion) {
        Procedure tramite = tramiteRepositoryPort.findById(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        stateMachine.remediateByApplicant(tramite, idSolicitante, detalleSubsanacion);

        return ProcedureMapper.toResponse(tramiteRepositoryPort.save(tramite));
    }
}
