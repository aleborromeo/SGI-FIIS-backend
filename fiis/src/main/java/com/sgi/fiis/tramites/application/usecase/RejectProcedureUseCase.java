package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.tramites.domain.service.ProcedureStateMachine;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RejectProcedureUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;
    private final ProcedureStateMachine stateMachine = new ProcedureStateMachine();

    public RejectProcedureUseCase(ProcedureRepositoryPort procedureRepositoryPort) {
        this.procedureRepositoryPort = procedureRepositoryPort;
    }

    @Transactional
    public ProcedureResponseDto execute(Long idTramite, RoleEnum rolEjecutor, Long idEjecutor) {
        Procedure tramite = procedureRepositoryPort.findById(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        switch (rolEjecutor) {
            case COORDINADOR_GRUPO      -> stateMachine.rechazarPorCoordinador(tramite, idEjecutor);
            case DIRECTOR_INVESTIGACION -> stateMachine.rechazarPorDirector(tramite, idEjecutor);
            default -> throw new BusinessException(
                    "El rol [" + rolEjecutor + "] no puede rechazar trámites");
        }

        return ProcedureMapper.toResponse(procedureRepositoryPort.save(tramite));
    }
}
