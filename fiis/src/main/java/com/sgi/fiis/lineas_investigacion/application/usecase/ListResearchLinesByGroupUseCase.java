package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.domain.port.ResearchLineRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListResearchLinesByGroupUseCase {

    private final ResearchLineRepositoryPort repository;

    public ListResearchLinesByGroupUseCase(ResearchLineRepositoryPort repository) {
        this.repository = repository;
    }

    public List<ResearchLine> execute(Integer groupId) {
        return repository.findActiveByGroup(groupId);
    }
}
