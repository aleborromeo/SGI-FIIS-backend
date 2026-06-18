package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataRepository;
    private final SpringDataRoleRepository roleRepository;

    public UserRepositoryAdapter(SpringDataUserRepository springDataRepository,
                                 SpringDataRoleRepository roleRepository) {
        this.springDataRepository = springDataRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByDni(String dni) {
        return springDataRepository.findByDni(dni).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String institutionalEmail) {
        return springDataRepository.findByInstitutionalEmail(institutionalEmail).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return springDataRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<User> search(String query) {
        return springDataRepository.search(query).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByDni(String dni) {
        return springDataRepository.existsByDni(dni);
    }

    @Override
    public boolean existsByEmail(String institutionalEmail) {
        return springDataRepository.existsByInstitutionalEmail(institutionalEmail);
    }

    // ========== Internal Mappers ==========

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .dni(entity.getDni())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .institutionalEmail(entity.getInstitutionalEmail())
                .phone(entity.getPhone())
                .passwordHash(entity.getPasswordHash())
                .active(entity.isActive())
                .mustChangePassword(entity.isMustChangePassword())
                .roleCode(entity.getRole().getRoleCode())
                .roleDescription(entity.getRole().getDescription())
                .oauthProvider(entity.getOauthProvider())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setDni(domain.getDni());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setInstitutionalEmail(domain.getInstitutionalEmail());
        entity.setPhone(domain.getPhone());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setActive(domain.isActive());
        entity.setMustChangePassword(domain.isMustChangePassword());
        entity.setOauthProvider(domain.getOauthProvider());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        // Find role entity by code
        RoleEntity roleEntity = roleRepository.findByRoleCode(domain.getRoleCode())
                .orElseThrow(() -> new RuntimeException("Role not found: " + domain.getRoleCode()));
        entity.setRole(roleEntity);

        return entity;
    }
}
