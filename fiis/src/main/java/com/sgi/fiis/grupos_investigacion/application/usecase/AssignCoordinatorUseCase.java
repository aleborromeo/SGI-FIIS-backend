package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignCoordinatorUseCase {

    private final ResearchGroupRepositoryPort groupRepository;

    public AssignCoordinatorUseCase(ResearchGroupRepositoryPort groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Transactional
    public ResearchGroup execute(Integer groupId, Integer userId) {
        ResearchGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("grupos.error.not-found", groupId));

        if (!groupRepository.existsActiveUserWithRole(userId, "COORDINADOR_GRUPO")) {
            throw new BusinessException("grupos.error.coordinator-invalid-role", userId);
        }

        group.setCurrentCoordinatorId(userId);
        return groupRepository.save(group);
    }
}
