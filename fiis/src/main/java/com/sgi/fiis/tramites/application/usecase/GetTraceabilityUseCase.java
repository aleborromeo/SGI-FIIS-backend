package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.ProcedureMovementResponseDto;
import com.sgi.fiis.tramites.application.mapper.ProcedureMapper;
import com.sgi.fiis.tramites.domain.port.ProcedureMovementRepositoryPort;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetTraceabilityUseCase {

    private final ProcedureRepositoryPort tramiteRepositoryPort;
    private final ProcedureMovementRepositoryPort movimientoRepositoryPort;

    public GetTraceabilityUseCase(ProcedureRepositoryPort tramiteRepositoryPort,
                                         ProcedureMovementRepositoryPort movimientoRepositoryPort) {
        this.tramiteRepositoryPort    = tramiteRepositoryPort;
        this.movimientoRepositoryPort = movimientoRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<ProcedureMovementResponseDto> execute(Long idTramite) {
        tramiteRepositoryPort.findById(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        return ProcedureMapper.toMovimientoResponseList(
                movimientoRepositoryPort.findByProcedureId(idTramite));
    }
}
