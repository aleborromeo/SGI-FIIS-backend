package com.sgi.fiis.lineas_investigacion.application.usecase;

import com.sgi.fiis.lineas_investigacion.domain.port.ResearchGroupLineRepositoryPort;
import com.sgi.fiis.grupos_investigacion.application.usecase.GetGroupUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignGroupToResearchLineUseCase {

    private final ResearchGroupLineRepositoryPort repository;
    private final GetResearchLineUseCase getResearchLineUseCase;
    private final GetGroupUseCase getGroupUseCase;

    public AssignGroupToResearchLineUseCase(ResearchGroupLineRepositoryPort repository,
                                            GetResearchLineUseCase getResearchLineUseCase,
                                            GetGroupUseCase getGroupUseCase) {
        this.repository = repository;
        this.getResearchLineUseCase = getResearchLineUseCase;
        this.getGroupUseCase = getGroupUseCase;
    }

    @Transactional
    public void execute(Integer lineId, Integer groupId) {
        getResearchLineUseCase.execute(lineId); // Validates line
        getGroupUseCase.execute(groupId);       // Validates group

        if (!repository.isGroupAssignedToLine(groupId, lineId)) {
            repository.assignGroupToLine(groupId, lineId);
        }
    }
}
