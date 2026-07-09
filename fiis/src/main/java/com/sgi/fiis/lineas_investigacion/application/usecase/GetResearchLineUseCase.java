package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetResearchLineUseCase {

    private final ResearchLineRepositoryPort repository;

    public GetResearchLineUseCase(ResearchLineRepositoryPort repository) {
        this.repository = repository;
    }

    public ResearchLine execute(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("lineas.error.not-found", id));
    }
}
