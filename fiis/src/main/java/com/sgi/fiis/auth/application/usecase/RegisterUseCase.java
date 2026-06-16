package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Caso de uso: Auto-registro de usuarios (Paso 1).
 * Valida los datos iniciales y el dominio del correo, y guarda temporalmente en memoria enviando un código de verificación.
 */
@Service
public class RegisterUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterUseCase.class);
    private final SecureRandom random = new SecureRandom();
    private final UsuarioRepositoryPort usuarioRepository;
    private final PendingRegistrationService pendingRegistrationService;
    private final EmailSenderPort emailSender;

    public RegisterUseCase(UsuarioRepositoryPort usuarioRepository,
                           PendingRegistrationService pendingRegistrationService,
                           EmailSenderPort emailSender) {
        this.usuarioRepository = usuarioRepository;
        this.pendingRegistrationService = pendingRegistrationService;
        this.emailSender = emailSender;
    }

    public void execute(RegisterRequestDto dto) {
        String correo = dto.getCorreoInstitucional().trim();

        // 1. Validar dominio .edu.pe del correo
        if (!correo.toLowerCase().endsWith(".edu.pe")) {
            throw new BusinessException("El correo institucional debe pertenecer al dominio .edu.pe");
        }

        // 2. Validar que no exista DNI duplicado en la base de datos
        if (usuarioRepository.existsByDni(dto.getDni())) {
            throw new DuplicateResourceException("Usuario", "DNI", dto.getDni());
        }

        // 3. Validar que no exista correo duplicado en la base de datos
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new DuplicateResourceException("Usuario", "correo", correo);
        }

        // 4. Generar código de 6 dígitos aleatorio
        int num = random.nextInt(900000) + 100000; // 100000 a 999999
        String code = String.valueOf(num);

        // 5. Almacenar temporalmente en memoria
        pendingRegistrationService.register(correo, dto, code);

        // 6. Enviar código por correo electrónico
        try {
            emailSender.sendVerificationCode(correo, code);
        } catch (Exception e) {
            log.error("[EMAIL SENDER] Error al enviar código de registro: {}", e.getMessage(), e);
        }

        // Imprimir en consola de desarrollo para pruebas fáciles
        String cleanCorreo = correo.replaceAll("[\n\r]", "_");
        log.info("CÓDIGO DE VERIFICACIÓN DE REGISTRO GENERADO (DEV) - Usuario: {}, Código: {}", cleanCorreo, code);
    }
}
