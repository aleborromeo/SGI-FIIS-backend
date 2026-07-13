package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetProcedureUseCase {

    private final ProcedureRepositoryPort procedureRepositoryPort;

    public GetProcedureUseCase(ProcedureRepositoryPort procedureRepositoryPort) {
        this.procedureRepositoryPort = procedureRepositoryPort;
    }

    @Transactional(readOnly = true)
    public ProcedureResponseDto execute(Long id) {
        var procedure = procedureRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", id));
        return ProcedureMapper.toResponse(procedure);
    }
}
