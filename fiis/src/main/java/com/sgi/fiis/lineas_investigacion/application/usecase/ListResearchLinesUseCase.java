package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListResearchLinesUseCase {

    private final ResearchLineRepositoryPort repository;

    public ListResearchLinesUseCase(ResearchLineRepositoryPort repository) {
        this.repository = repository;
    }

    public List<ResearchLine> execute(boolean onlyActive) {
        if (onlyActive) {
            return repository.findAllActive();
        }
        return repository.findAll();
    }
}
