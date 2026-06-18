package com.sgi.fiis.tramites.application.usecase;

import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.tramites.application.dto.MovimientoResponseDto;
import com.sgi.fiis.tramites.application.mapper.TramiteMapper;
import com.sgi.fiis.tramites.domain.port.MovimientoTramiteRepositoryPort;
import com.sgi.fiis.tramites.domain.port.TramiteRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultarTrazabilidadUseCase {

    private final TramiteRepositoryPort tramiteRepositoryPort;
    private final MovimientoTramiteRepositoryPort movimientoRepositoryPort;

    public ConsultarTrazabilidadUseCase(TramiteRepositoryPort tramiteRepositoryPort,
                                         MovimientoTramiteRepositoryPort movimientoRepositoryPort) {
        this.tramiteRepositoryPort    = tramiteRepositoryPort;
        this.movimientoRepositoryPort = movimientoRepositoryPort;
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponseDto> execute(Long idTramite) {
        tramiteRepositoryPort.buscarPorId(idTramite)
                .orElseThrow(() -> new ResourceNotFoundException("Trámite", "id", idTramite));

        return TramiteMapper.toMovimientoResponseList(
                movimientoRepositoryPort.buscarPorIdTramite(idTramite));
    }
}
