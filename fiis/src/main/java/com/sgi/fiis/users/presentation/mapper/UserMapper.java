package com.sgi.fiis.users.presentation.mapper;

import com.sgi.fiis.users.application.dto.UserRequestDto;
import com.sgi.fiis.users.application.dto.UserResponseDto;
import com.sgi.fiis.users.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserRequestDto dto) {
        return User.builder()
                .dni(dto.getDni())
                .firstNames(dto.getFirstNames())
                .lastNames(dto.getLastNames())
                .institutionalEmail(dto.getInstitutionalEmail())
                .phone(dto.getPhone())
                .roleCode(dto.getRoleCode())
                .build();
    }

    public UserResponseDto toResponseDto(User domain) {
        if (domain == null) {
            return null;
        }
        return UserResponseDto.builder()
                .id(domain.getId())
                .dni(domain.getDni())
                .firstNames(domain.getFirstNames())
                .lastNames(domain.getLastNames())
                .institutionalEmail(domain.getInstitutionalEmail())
                .phone(domain.getPhone())
                .active(domain.isActive())
                .mustChangePassword(domain.isMustChangePassword())
                .roleCode(domain.getRoleCode())
                .roleDescription(domain.getRoleDescription())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt().toString() : null)
                .updatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt().toString() : null)
                .build();
    }
}
