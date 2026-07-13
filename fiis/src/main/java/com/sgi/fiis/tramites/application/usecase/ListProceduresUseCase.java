package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListProceduresUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;

    public ListProceduresUseCase(ProcedureRepositoryPort procedureRepositoryPort) {
        this.procedureRepositoryPort = procedureRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<ProcedureResponseDto> execute() {
        return procedureRepositoryPort.findAll().stream()
                .map(ProcedureMapper::toResponse)
                .toList();
    }
}
