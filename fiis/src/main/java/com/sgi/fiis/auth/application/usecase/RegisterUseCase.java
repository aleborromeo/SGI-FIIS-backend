package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use case: Self-registration of users (Step 1).
 * Validates initial data and email domain, and temporarily saves in memory while sending a verification code.
 */
@Service
public class RegisterUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterUseCase.class);
    private final SecureRandom random = new SecureRandom();
    private final UserRepositoryPort userRepository;
    private final PendingRegistrationService pendingRegistrationService;
    private final EmailSenderPort emailSender;

    public RegisterUseCase(UserRepositoryPort userRepository,
                           PendingRegistrationService pendingRegistrationService,
                           EmailSenderPort emailSender) {
        this.userRepository = userRepository;
        this.pendingRegistrationService = pendingRegistrationService;
        this.emailSender = emailSender;
    }

    public void execute(RegisterRequestDto dto) {
        // 0. Validate passwords match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("auth.password.mismatch");
        }

        String email = dto.getInstitutionalEmail().trim();

        // 1. Validate email domain ends with .edu.pe
        if (!email.toLowerCase().endsWith(".edu.pe")) {
            throw new BusinessException("auth.email.invalid-domain");
        }

        // 2. Validate DNI uniqueness in DB
        if (userRepository.existsByDni(dto.getDni())) {
            throw new DuplicateResourceException("Usuario", "DNI", dto.getDni());
        }

        // 3. Validate email uniqueness in DB
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Usuario", "correo", email);
        }

        // 4. Generate 6-digit random code
        int num = random.nextInt(900000) + 100000; // 100000 to 999999
        String code = String.valueOf(num);

        // 5. Store temporarily in memory
        pendingRegistrationService.register(email, dto, code);

        // 6. Send code by email
        emailSender.sendVerificationCode(email, code);

        // Print to development console for easy testing
        String cleanEmail = email.replaceAll("[\n\r]", "_");
        log.info("CÓDIGO DE VERIFICACIÓN DE REGISTRO GENERADO (DEV) - Usuario: {}, Código: {}", cleanEmail, code);
    }
}
