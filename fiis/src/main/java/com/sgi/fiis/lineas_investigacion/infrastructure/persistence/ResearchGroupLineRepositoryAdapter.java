package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import com.sgi.fiis.lineas_investigacion.domain.port.ResearchGroupLineRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class ResearchGroupLineRepositoryAdapter implements ResearchGroupLineRepositoryPort {

    private final SpringDataResearchGroupLineRepository repository;

    public ResearchGroupLineRepositoryAdapter(SpringDataResearchGroupLineRepository repository) {
        this.repository = repository;
    }

    @Override
    public void assignGroupToLine(Integer groupId, Integer lineId) {
        repository.save(new ResearchGroupLineEntity(groupId, lineId));
    }

    @Override
    public void removeGroupFromLine(Integer groupId, Integer lineId) {
        repository.deleteById(new ResearchGroupLineId(groupId, lineId));
    }

    @Override
    public boolean isGroupAssignedToLine(Integer groupId, Integer lineId) {
        return repository.existsByGroupIdAndLineId(groupId, lineId);
    }
}
