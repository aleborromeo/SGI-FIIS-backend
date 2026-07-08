package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveMemberUseCase {

    private final MembershipRepositoryPort membershipRepository;

    public RemoveMemberUseCase(MembershipRepositoryPort membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public Membership execute(Integer groupId, Integer userId) {
        Membership membership = membershipRepository.findActiveByUserInGroup(userId, groupId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active membership found for user " + userId
                        + " in group " + groupId));
        membership.remove();
        return membershipRepository.save(membership);
    }
}
