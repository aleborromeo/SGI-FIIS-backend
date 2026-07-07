package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListResearchGroupsByLineUseCase {

    private final ResearchGroupRepositoryPort repository;

    public ListResearchGroupsByLineUseCase(ResearchGroupRepositoryPort repository) {
        this.repository = repository;
    }

    public List<ResearchGroup> execute(Integer lineId) {
        return repository.findGroupsByLineId(lineId);
    }
}
