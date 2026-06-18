package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ObtenerLineaUseCase {

    private final LineaInvestigacionRepositoryPort lineaRepository;

    public ObtenerLineaUseCase(LineaInvestigacionRepositoryPort lineaRepository) {
        this.lineaRepository = lineaRepository;
    }

    public LineaInvestigacion execute(Integer id) {
        return lineaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LineaInvestigacion", "id", id));
    }
}
