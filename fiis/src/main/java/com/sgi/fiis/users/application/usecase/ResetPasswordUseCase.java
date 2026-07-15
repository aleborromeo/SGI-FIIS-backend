package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

/**
 * Use case: Reset a user's password (RF-12).
 * Generates a secure random password and sends it to the user's email.
 */
@Service
public class ResetPasswordUseCase {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordUseCase.class);

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailSenderPort emailSender;

    public ResetPasswordUseCase(UserRepositoryPort userRepository,
                                PasswordEncoderPort passwordEncoder,
                                EmailSenderPort emailSender) {
        this.userRepository = userRepository;
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
    public void execute(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Generate a secure random password
        String rawPassword = generateSecurePassword();

        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.markPasswordChangeRequired();

        userRepository.save(user);

        // Send new credentials to the user's email
        try {
            emailSender.sendNewUserCredentials(user.getInstitutionalEmail(), rawPassword);
            log.info("Contraseña restablecida y enviada por correo a: {}", user.getInstitutionalEmail());
        } catch (Exception e) {
            log.error("Error al enviar credenciales de restablecimiento al correo {}: {}",
                    user.getInstitutionalEmail(), e.getMessage(), e);
        }
    }
}

