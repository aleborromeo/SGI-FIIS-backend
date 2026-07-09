package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.ResendCodeRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

/**
 * Use case: Resend verification code (Step 1.5).
 * Validates the existence of the pending registration and regenerates a new code to send it.
 */
@Service
public class ResendCodeUseCase {

    private static final Logger log = LoggerFactory.getLogger(ResendCodeUseCase.class);
    private final SecureRandom random = new SecureRandom();
    private final PendingRegistrationService pendingRegistrationService;
    private final EmailSenderPort emailSender;

    public ResendCodeUseCase(PendingRegistrationService pendingRegistrationService,
                             EmailSenderPort emailSender) {
        this.pendingRegistrationService = pendingRegistrationService;
        this.emailSender = emailSender;
    }

    public void execute(ResendCodeRequestDto dto) {
        String email = dto.getEmail().trim();

        // 1. Validate that the pending registration exists in memory
        PendingRegistrationService.PendingRegistration pending = pendingRegistrationService.get(email);
        if (pending == null) {
            throw new BusinessException("auth.register.pending-not-found");
        }

        // 2. Generate a new 6-digit random code
        int num = random.nextInt(900000) + 100000; // 100000 to 999999
        String newCode = String.valueOf(num);

        // 3. Update temporarily in memory (retains original details but renews the code and expiry time)
        pendingRegistrationService.register(email, pending.getRequestDto(), newCode);

        // 4. Send code by email
        emailSender.sendVerificationCode(email, newCode);

        // Print to development console for easy testing
        String cleanEmail = email.replaceAll("[\n\r]", "_");
        log.info("CÓDIGO DE VERIFICACIÓN REENVIADO (DEV) - Usuario: {}, Código: {}", cleanEmail, newCode);
    }
}
