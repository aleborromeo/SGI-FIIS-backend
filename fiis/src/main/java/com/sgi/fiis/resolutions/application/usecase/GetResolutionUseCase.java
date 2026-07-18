package com.sgi.fiis.resolutions.application.usecase;

import com.sgi.fiis.resolutions.application.dto.ResolutionResponseDTO;
import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.out.ResolutionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetResolutionUseCase {

    private final ResolutionRepositoryPort resolutionRepositoryPort;

    public GetResolutionUseCase(ResolutionRepositoryPort resolutionRepositoryPort) {
        this.resolutionRepositoryPort = resolutionRepositoryPort;
    }

    public Optional<ResolutionResponseDTO> execute(Long id) {
        return resolutionRepositoryPort.findById(id)
                .map(this::toDto);
    }

    public Optional<ResolutionResponseDTO> findByProcedureId(Long procedureId) {
        return resolutionRepositoryPort.findByProcedureId(procedureId)
                .map(this::toDto);
    }

    private ResolutionResponseDTO toDto(Resolution resolution) {
        return new ResolutionResponseDTO(
                resolution.idResolucion(),
                resolution.numeroResolucion(),
                resolution.fechaEmision(),
                resolution.asunto(),
                resolution.idTramite(),
                resolution.idDocumentoAdjunto(),
                resolution.fechaRegistro()
        );
    }
}
