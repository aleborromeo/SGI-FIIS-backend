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
            throw new DuplicateResourceException("lineas.error.duplicate-name", line.getLineName());
        }
        line.setActive(true);
        line.setCreatedAt(LocalDateTime.now(java.time.ZoneId.systemDefault()));
        line.setUpdatedAt(LocalDateTime.now(java.time.ZoneId.systemDefault()));
        return repository.save(line);
    }
}
