package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateGroupUseCase {

    private final ResearchGroupRepositoryPort groupRepository;

    public CreateGroupUseCase(ResearchGroupRepositoryPort groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional
    public ResearchGroup execute(ResearchGroup group) {
        if (groupRepository.existsByCode(group.getGroupCode())) {
            throw new DuplicateResourceException("grupos.error.duplicate-code", group.getGroupCode());
        }
        group.setActive(true);
        return groupRepository.save(group);
    }
}
