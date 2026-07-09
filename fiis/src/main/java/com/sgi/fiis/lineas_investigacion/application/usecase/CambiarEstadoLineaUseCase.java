package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.domain.port.LineaInvestigacionRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CambiarEstadoLineaUseCase {

    private final LineaInvestigacionRepositoryPort lineaRepository;

    public CambiarEstadoLineaUseCase(LineaInvestigacionRepositoryPort lineaRepository) {
        this.lineaRepository = lineaRepository;
    }

    @Transactional
    public LineaInvestigacion execute(Integer id, boolean activar) {
        LineaInvestigacion linea = lineaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("lineas.error.not-found", id));
        if (activar) {
            linea.activar();
        } else {
            linea.desactivar();
        }
        return lineaRepository.save(linea);
    }
}
