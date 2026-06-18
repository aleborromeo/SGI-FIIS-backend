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
 * Caso de uso: Reenviar código de verificación (Paso 1.5).
 * Valida la existencia del registro pendiente y regenera un nuevo código para enviarlo.
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
        String correo = dto.getCorreo().trim();

        // 1. Validar que exista el registro pendiente en memoria
        PendingRegistrationService.PendingRegistration pending = pendingRegistrationService.get(correo);
        if (pending == null) {
            throw new BusinessException("No se encontró ningún registro pendiente para el correo especificado");
        }

        // 2. Generar un nuevo código de 6 dígitos aleatorio
        int num = random.nextInt(900000) + 100000; // 100000 a 999999
        String newCode = String.valueOf(num);

        // 3. Actualizar temporalmente en memoria (conservando los datos originales pero renovando el código y tiempo)
        pendingRegistrationService.register(correo, pending.getRequestDto(), newCode);

        // 4. Enviar código por correo electrónico
        emailSender.sendVerificationCode(correo, newCode);

        // Imprimir en consola de desarrollo para pruebas fáciles
        String cleanCorreo = correo.replaceAll("[\n\r]", "_");
        log.info("CÓDIGO DE VERIFICACIÓN REENVIADO (DEV) - Usuario: {}, Código: {}", cleanCorreo, newCode);
    }
}
