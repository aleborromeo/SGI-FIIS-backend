package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Role;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.RoleRepositoryPort;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Use Case: Edit user data (RF-08).
 */
@Service
public class EditUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;

    public EditUserUseCase(UserRepositoryPort userRepository,
                           RoleRepositoryPort roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User execute(Long id, String firstName, String lastName,
                        String email, String phone, String roleCode) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (firstName != null && !firstName.isBlank()) {
            user.setFirstNames(firstName);
        }
        if (lastName != null && !lastName.isBlank()) {
            user.setLastNames(lastName);
        }
        if (email != null && !email.isBlank()) {
            if (!email.toLowerCase().endsWith(".edu.pe")) {
                throw new BusinessException("The institutional email must belong to the .edu.pe domain");
            }
            // Verify no other user has that email
            userRepository.findByEmail(email).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DuplicateResourceException("User", "email", email);
                }
            });
            user.setInstitutionalEmail(email);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        if (roleCode != null && !roleCode.isBlank()) {
            Role role = roleRepository.findByCode(roleCode)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "code", roleCode));
            user.setRoleCode(role.getCode());
        }

        user.setUpdatedAt(LocalDateTime.now(java.time.ZoneId.systemDefault()));
        return userRepository.save(user);
    }
}
