package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListGroupsUseCase {

    private final ResearchGroupRepositoryPort groupRepository;

    public ListGroupsUseCase(ResearchGroupRepositoryPort groupRepository) {
        this.groupRepository = groupRepository;
    }

    public List<ResearchGroup> execute() {
        return groupRepository.findAll();
    }
}
