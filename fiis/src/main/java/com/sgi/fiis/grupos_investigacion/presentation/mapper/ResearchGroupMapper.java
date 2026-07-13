package com.sgi.fiis.grupos_investigacion.presentation.mapper;

import com.sgi.fiis.grupos_investigacion.application.dto.ResearchGroupRequestDto;
import com.sgi.fiis.grupos_investigacion.application.dto.ResearchGroupResponseDto;
import com.sgi.fiis.grupos_investigacion.application.dto.MembershipResponseDto;
import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;
import com.sgi.fiis.grupos_investigacion.domain.model.Membership;
import org.springframework.stereotype.Component;

@Component
public class ResearchGroupMapper {

    public ResearchGroup toDomain(ResearchGroupRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return ResearchGroup.builder()
                .groupCode(dto.getGroupCode())
                .groupName(dto.getGroupName())
                .build();
    }

    public ResearchGroupResponseDto toResponseDto(ResearchGroup domain) {
        if (domain == null) {
            return null;
        }
        return ResearchGroupResponseDto.builder()
                .id(domain.getId())
                .groupCode(domain.getGroupCode())
                .groupName(domain.getGroupName())
                .currentCoordinatorId(domain.getCurrentCoordinatorId())
                .coordinatorFirstNames(domain.getCoordinatorFirstNames())
                .coordinatorLastNames(domain.getCoordinatorLastNames())
                .active(domain.isActive())
                .build();
    }

    public MembershipResponseDto toMembershipResponseDto(Membership membership) {
        if (membership == null) {
            return null;
        }
        return MembershipResponseDto.builder()
                .id(membership.getId())
                .groupId(membership.getGroupId())
                .userId(membership.getUserId())
                .userFirstNames(membership.getUserFirstNames())
                .userLastNames(membership.getUserLastNames())
                .userEmail(membership.getUserEmail())
                .userRoleCode(membership.getUserRoleCode())
                .active(membership.isActive())
                .startDate(membership.getStartDate() != null ? membership.getStartDate().toString() : null)
                .endDate(membership.getEndDate() != null ? membership.getEndDate().toString() : null)
                .build();
    }
}
