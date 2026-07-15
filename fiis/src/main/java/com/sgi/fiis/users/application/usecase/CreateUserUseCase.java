package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.shared.domain.utils.PasswordGenerator;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.RoleRepositoryPort;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import java.time.ZoneId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Use case: Create user (RF-07).
 * - Generates institutional email automatically if not provided (RF-10).
 * - Initial password is the user's DNI, hashed with BCrypt.
 */
@Service
public class CreateUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final com.sgi.fiis.auth.domain.port.EmailSenderPort emailSender;

    public CreateUserUseCase(UserRepositoryPort userRepository,
                             RoleRepositoryPort roleRepository,
                             PasswordEncoderPort passwordEncoder,
                             com.sgi.fiis.auth.domain.port.EmailSenderPort emailSender) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }

    @Transactional
    public User execute(User user) {
        // Validate that the role exists
        if (user.getRoleCode() == null) {
            throw new BusinessException("El código de rol es obligatorio");
        }
        var role = roleRepository.findByCode(user.getRoleCode())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "código", user.getRoleCode()));

        // Validate that DNI is exactly 8 digits
        if (user.getDni() == null || !user.getDni().matches("\\d{8}")) {
            throw new BusinessException("El DNI debe tener exactamente 8 dígitos numéricos");
        }

        // Validate DNI uniqueness (RNF-38)
        if (userRepository.existsByDni(user.getDni())) {
            throw new DuplicateResourceException("Usuario", "DNI", user.getDni());
        }

        // Generate institutional email if not provided (RF-10)
        user.generateInstitutionalEmail();

        // Validate that the institutional email ends with .edu.pe
        if (user.getInstitutionalEmail() == null || !user.getInstitutionalEmail().toLowerCase().endsWith(".edu.pe")) {
            throw new BusinessException("El correo institucional debe pertenecer al dominio .edu.pe");
        }

        // Validate email uniqueness (RNF-38)
        if (userRepository.existsByEmail(user.getInstitutionalEmail())) {
            throw new DuplicateResourceException("Usuario", "correo", user.getInstitutionalEmail());
        }

        // Generate secure temporary password
        String rawPassword = PasswordGenerator.generateSecurePassword();
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setTemporaryPassword(rawPassword);
        user.setActive(true);
        user.setMustChangePassword(true);
        user.setCreatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        user.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));

        User savedUser = userRepository.save(user);

        // Send email with credentials
        try {
            emailSender.sendNewUserCredentials(savedUser.getInstitutionalEmail(), rawPassword);
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(CreateUserUseCase.class)
                .error("Error al enviar credenciales al correo del usuario: " + e.getMessage(), e);
        }

        return savedUser;
    }
}
