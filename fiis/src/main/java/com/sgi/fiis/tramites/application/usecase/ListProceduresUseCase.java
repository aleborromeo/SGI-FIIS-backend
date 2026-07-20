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

    @SuppressWarnings("java:S1172")
    public List<ProcedureResponseDto> execute(RoleEnum rolUsuario) {
        return execute();
    }

    @SuppressWarnings("java:S1172")
    public List<ProcedureResponseDto> execute(RoleEnum rolUsuario, Long userId) {
        return execute();
    }
}
