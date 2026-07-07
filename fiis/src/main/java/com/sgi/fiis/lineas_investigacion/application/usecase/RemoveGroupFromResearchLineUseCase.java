package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.port.ResearchGroupLineRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveGroupFromResearchLineUseCase {

    private final ResearchGroupLineRepositoryPort repository;
    private final GetResearchLineUseCase getResearchLineUseCase;

    public RemoveGroupFromResearchLineUseCase(ResearchGroupLineRepositoryPort repository,
                                              GetResearchLineUseCase getResearchLineUseCase) {
        this.repository = repository;
        this.getResearchLineUseCase = getResearchLineUseCase;
    }

    @Transactional
    public void execute(Integer lineId, Integer groupId) {
        getResearchLineUseCase.execute(lineId); // Validates line

        if (repository.isGroupAssignedToLine(groupId, lineId)) {
            repository.removeGroupFromLine(groupId, lineId);
        }
    }
}
