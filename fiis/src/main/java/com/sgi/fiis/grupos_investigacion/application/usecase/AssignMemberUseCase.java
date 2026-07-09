package com.sgi.fiis.grupos_investigacion.application.usecase;

import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import com.sgi.fiis.grupos_investigacion.domain.port.ResearchGroupRepositoryPort;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AssignMemberUseCase {

    private final ResearchGroupRepositoryPort groupRepository;
    private final MembershipRepositoryPort membershipRepository;

    public AssignMemberUseCase(ResearchGroupRepositoryPort groupRepository,
                               MembershipRepositoryPort membershipRepository) {
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public Membership execute(Integer groupId, Integer userId) {
        if (groupRepository.findById(groupId).isEmpty()) {
            throw new ResourceNotFoundException("ResearchGroup", "id", groupId);
        }

        if (!groupRepository.existsActiveUserWithRole(userId, "DOCENTE_INVESTIGADOR") &&
            !groupRepository.existsActiveUserWithRole(userId, "ESTUDIANTE")) {
            throw new BusinessException("User with id " + userId + " does not exist, is not active, or does not have the DOCENTE_INVESTIGADOR or ESTUDIANTE role");
        }

        if (membershipRepository.existsActiveByUser(userId)) {
            throw new BusinessException("User with id " + userId + " already belongs to an active research group (RF-21)");
        }

        Membership membership = Membership.builder()
                .groupId(groupId)
                .userId(userId)
                .active(true)
                .startDate(LocalDateTime.now(java.time.ZoneId.systemDefault()))
                .build();

        return membershipRepository.save(membership);
    }
}
