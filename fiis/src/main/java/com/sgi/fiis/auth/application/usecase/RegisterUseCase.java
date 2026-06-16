package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Auto-registro de usuarios (Paso 1).
 * Valida los datos iniciales y el dominio del correo, y guarda temporalmente en memoria enviando un código de verificación.
 */
@Service
public class RegisterUseCase {

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
        int num = new java.util.Random().nextInt(900000) + 100000; // 100000 a 999999
        String code = String.valueOf(num);

        // 5. Almacenar temporalmente en memoria
        pendingRegistrationService.register(correo, dto, code);

        // 6. Enviar código por correo electrónico
        try {
            emailSender.sendVerificationCode(correo, code);
        } catch (Exception e) {
            System.err.println("[EMAIL SENDER] Error al enviar código de registro: " + e.getMessage());
        }

        // Imprimir en consola de desarrollo para pruebas fáciles
        System.out.println("\n==================================================");
        System.out.println("CÓDIGO DE VERIFICACIÓN DE REGISTRO GENERADO (DEV):");
        System.out.println("Usuario: " + correo);
        System.out.println("Código: " + code);
        System.out.println("==================================================\n");
    }
}
