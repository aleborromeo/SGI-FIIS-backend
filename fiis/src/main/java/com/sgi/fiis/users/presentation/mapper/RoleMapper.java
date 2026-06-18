package com.sgi.fiis.users.presentation.mapper;

import com.sgi.fiis.users.application.dto.RoleResponseDto;
import com.sgi.fiis.users.domain.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleResponseDto toResponseDto(Role domain) {
        if (domain == null) {
            return null;
        }
        return RoleResponseDto.builder()
                .id(domain.getId())
                .code(domain.getCode())
                .description(domain.getDescription())
                .build();
    }
}
