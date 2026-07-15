package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
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

    private static final java.security.SecureRandom SECURE_RANDOM = new java.security.SecureRandom();

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

    private String generateSecurePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        String all = upper + lower + digits + symbols;
        
        StringBuilder sb = new StringBuilder();
        
        // Ensure at least one of each required type
        sb.append(upper.charAt(SECURE_RANDOM.nextInt(upper.length())));
        sb.append(lower.charAt(SECURE_RANDOM.nextInt(lower.length())));
        sb.append(digits.charAt(SECURE_RANDOM.nextInt(digits.length())));
        sb.append(symbols.charAt(SECURE_RANDOM.nextInt(symbols.length())));
        
        // Fill rest up to 10 characters
        for (int i = 0; i < 6; i++) {
            sb.append(all.charAt(SECURE_RANDOM.nextInt(all.length())));
        }
        
        // Shuffle the characters
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = SECURE_RANDOM.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        
        return new String(chars);
    }

    @Transactional
    public User execute(User user) {
        // Validate that the role exists
        if (roleRepository.findByCode(user.getRoleCode()).isEmpty()) {
            throw new ResourceNotFoundException("Rol", "codigo", user.getRoleCode());
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
        String rawPassword = generateSecurePassword();
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
