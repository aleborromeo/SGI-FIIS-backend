package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Role;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.RoleRepositoryPort;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZoneId;

import java.time.LocalDateTime;

/**
 * Use case: Update user details (RF-08).
 */
@Service
public class UpdateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;

    public UpdateUserUseCase(UserRepositoryPort userRepository,
                             RoleRepositoryPort roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    @Auditable(action = "UPDATE_USER", table = "usuarios")
    public User execute(Long id, String firstNames, String lastNames,
                        String institutionalEmail, String phone, String roleCode) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (firstNames != null && !firstNames.isBlank()) {
            user.setFirstNames(firstNames);
        }
        if (lastNames != null && !lastNames.isBlank()) {
            user.setLastNames(lastNames);
        }
        if (institutionalEmail != null && !institutionalEmail.isBlank()) {
            if (!institutionalEmail.toLowerCase().endsWith(".edu.pe")) {
                throw new BusinessException("El correo institucional debe pertenecer al dominio .edu.pe");
            }
            // Check that no other user has this email
            userRepository.findByEmail(institutionalEmail).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DuplicateResourceException("Usuario", "correo", institutionalEmail);
                }
            });
            user.setInstitutionalEmail(institutionalEmail);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (roleCode != null && !roleCode.isBlank()) {
            Role role = roleRepository.findByCode(roleCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "codigo", roleCode));
            user.setRoleCode(role.getCode());
        }

        user.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        return userRepository.save(user);
    }
}
