package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.Role;
import com.sgi.fiis.users.domain.port.RoleRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final SpringDataRoleRepository springDataRepository;

    public RoleRepositoryAdapter(SpringDataRoleRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return springDataRepository.findByCode(code)
                .map(this::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return springDataRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Role toDomain(RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .build();
    }
}
