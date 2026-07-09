package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeResearchLineStatusUseCase {

    private final ResearchLineRepositoryPort repository;

    public ChangeResearchLineStatusUseCase(ResearchLineRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public ResearchLine execute(Integer id, boolean active) {
        ResearchLine line = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("lineas.error.not-found", id));
        if (active) {
            line.activate();
        } else {
            line.deactivate();
        }
        return repository.save(line);
    }
}
