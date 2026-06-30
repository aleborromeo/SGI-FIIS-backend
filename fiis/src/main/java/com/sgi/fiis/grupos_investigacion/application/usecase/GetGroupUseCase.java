package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetGroupUseCase {

    private final ResearchGroupRepositoryPort groupRepository;

    public GetGroupUseCase(ResearchGroupRepositoryPort groupRepository) {
        this.groupRepository = groupRepository;
    }

    public ResearchGroup execute(Integer id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResearchGroup", "id", id));
    }
}
