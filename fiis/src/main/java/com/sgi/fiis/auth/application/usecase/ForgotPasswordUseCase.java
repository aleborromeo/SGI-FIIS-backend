package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.service.PendingResetPasswordService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

/**
 * Caso de uso: Solicitar recuperación de contraseña (envío de código por correo).
 */
@Service
public class ForgotPasswordUseCase {

    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordUseCase.class);
    private final SecureRandom random = new SecureRandom();
    
    private final UserRepositoryPort userRepository;
    private final PendingResetPasswordService pendingResetPasswordService;
    private final EmailSenderPort emailSender;

    public ForgotPasswordUseCase(UserRepositoryPort userRepository,
                                 PendingResetPasswordService pendingResetPasswordService,
                                 EmailSenderPort emailSender) {
        this.userRepository = userRepository;
        this.pendingResetPasswordService = pendingResetPasswordService;
        this.emailSender = emailSender;
    }

    public void execute(String email) {
        String cleanEmail = email != null ? email.trim() : "";

        // 1. Validar que el usuario existe en el sistema
        if (!userRepository.existsByEmail(cleanEmail)) {
            throw new BusinessException("auth.user.not-found");
        }

        // 2. Generar un código aleatorio de 6 dígitos
        int num = random.nextInt(900000) + 100000; // 100000 a 999999
        String code = String.valueOf(num);

        // 3. Almacenar temporalmente en memoria
        pendingResetPasswordService.register(cleanEmail, code);

        // 4. Enviar código por correo
        emailSender.sendPasswordResetCode(cleanEmail, code);

        // Imprimir en consola de desarrollo para pruebas rápidas
        String safeEmail = cleanEmail.replaceAll("[\n\r]", "_");
        log.info("CÓDIGO DE RESTABLECIMIENTO DE CONTRASEÑA GENERADO (DEV) - Usuario: {}, Código: {}", safeEmail, code);
    }
}
