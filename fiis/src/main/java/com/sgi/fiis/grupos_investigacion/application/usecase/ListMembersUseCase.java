package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListMembersUseCase {

    private final ResearchGroupRepositoryPort groupRepository;
    private final MembershipRepositoryPort membershipRepository;

    public ListMembersUseCase(ResearchGroupRepositoryPort groupRepository,
                             MembershipRepositoryPort membershipRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
    }

    public List<Membership> execute(Integer groupId) {
        if (groupRepository.findById(groupId).isEmpty()) {
            throw new ResourceNotFoundException("grupos.error.not-found", groupId);
        }
        return membershipRepository.findActiveByGroup(groupId);
    }
}
