package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegisterResearchLineUseCase {

    private final ResearchLineRepositoryPort repository;

    public RegisterResearchLineUseCase(ResearchLineRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public ResearchLine execute(ResearchLine line) {
        if (repository.existsByName(line.getLineName())) {
            throw new DuplicateResourceException("ResearchLine", "name", line.getLineName());
        }
        line.setActive(true);
        line.setCreatedAt(LocalDateTime.now());
        line.setUpdatedAt(LocalDateTime.now());
        return repository.save(line);
    }
}
