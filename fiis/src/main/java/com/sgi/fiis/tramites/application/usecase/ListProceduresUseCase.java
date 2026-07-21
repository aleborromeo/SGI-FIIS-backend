package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import com.sgi.fiis.users.domain.model.RoleEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListProceduresUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;

    public ListProceduresUseCase(ProcedureRepositoryPort procedureRepositoryPort) {
        this.procedureRepositoryPort = procedureRepositoryPort;
    }

    public List<ProcedureResponseDto> execute() {
        return procedureRepositoryPort.findAll().stream()
                .map(ProcedureMapper::toResponse)
                .toList();
    }

    public List<ProcedureResponseDto> execute(RoleEnum rolUsuario) {
        return execute(rolUsuario, null);
    }

    public List<ProcedureResponseDto> execute(RoleEnum rolUsuario, Long userId) {
        if (rolUsuario == null) {
            return execute();
        }
        return procedureRepositoryPort.findByReviewerRole(rolUsuario).stream()
                .filter(t -> t.getCurrentStatus() != null && !t.getCurrentStatus().isTerminalStatus())
                .map(ProcedureMapper::toResponse)
                .toList();
    }
}
