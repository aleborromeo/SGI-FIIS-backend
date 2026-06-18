package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateLineaInvestigacionUseCase {

    private final ResearchLineRepositoryPort repository;

    public DeactivateLineaInvestigacionUseCase(ResearchLineRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public ResearchLine execute(Integer id) {
        ResearchLine line = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResearchLine", "id", id));
        line.deactivate();
        return repository.save(line);
    }
}
