package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.shared.domain.utils.PasswordGenerator;
import com.sgi.fiis.shared.infrastructure.aspect.Auditable;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case: Reset a user's password (RF-12).
 * Generates a secure random password and sends it to the user's email.
 */
@Service
public class ResetPasswordUseCase {

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

    @Transactional
    @Auditable(action = "RESET_PASSWORD", table = "usuarios")
    public void execute(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Generate a secure random password
        String rawPassword = PasswordGenerator.generateSecurePassword();

        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.markPasswordChangeRequired();

        userRepository.save(user);

        // Send new credentials to the user's email
        try {
            emailSender.sendNewUserCredentials(user.getInstitutionalEmail(), rawPassword);
            log.info("Contraseña restablecida y enviada por correo");
        } catch (Exception e) {
            log.error("Error al enviar credenciales de restablecimiento", e);
        }
    }
}

