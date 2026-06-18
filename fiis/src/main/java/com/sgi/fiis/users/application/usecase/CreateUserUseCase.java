package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.RoleRepositoryPort;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Use Case: Create user (RF-07).
 * - Generates institutional email automatically if not provided (RF-10).
 * - The initial password is the user's DNI, hashed with BCrypt.
 */
@Service
public class CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;

    public CreateUserUseCase(UserRepositoryPort userRepository,
                             RoleRepositoryPort roleRepository,
                             PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User execute(User user) {
        // Validate that the role exists
        roleRepository.findByCode(user.getRoleCode())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "code", user.getRoleCode()));

        // Validate DNI duplicate (RNF-38)
        if (userRepository.existsByDni(user.getDni())) {
            throw new DuplicateResourceException("User", "DNI", user.getDni());
        }

        // Generate institutional email if not provided (RF-10)
        user.generateInstitutionalEmail();

        // Validate that institutional email ends with .edu.pe
        if (user.getInstitutionalEmail() == null || !user.getInstitutionalEmail().toLowerCase().endsWith(".edu.pe")) {
            throw new BusinessException("The institutional email must belong to the .edu.pe domain");
        }

        // Validate email duplicate (RNF-38)
        if (userRepository.existsByEmail(user.getInstitutionalEmail())) {
            throw new DuplicateResourceException("User", "email", user.getInstitutionalEmail());
        }

        // Initial password = hashed DNI
        user.setPasswordHash(passwordEncoder.encode(user.getDni()));
        user.setActive(true);
        user.setMustChangePassword(true);
        user.setCreatedAt(LocalDateTime.now(java.time.ZoneId.systemDefault()));
        user.setUpdatedAt(LocalDateTime.now(java.time.ZoneId.systemDefault()));

        return userRepository.save(user);
    }
}
