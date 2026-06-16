package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Caso de uso: Verificar auto-registro (Paso 2).
 * Valida el código de verificación temporal. Si es correcto, persiste al usuario en base de datos y le genera el JWT final.
 */
@Service
public class VerifyRegistrationUseCase {

    private final PendingRegistrationService pendingRegistrationService;
    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public VerifyRegistrationUseCase(PendingRegistrationService pendingRegistrationService,
                                      UsuarioRepositoryPort usuarioRepository,
                                      PasswordEncoderPort passwordEncoder,
                                      TokenProviderPort tokenProvider) {
        this.pendingRegistrationService = pendingRegistrationService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public LoginResponseDto execute(String correo, String codigo) {
        PendingRegistrationService.PendingRegistration pending = pendingRegistrationService.get(correo);

        // 1. Validar que exista un registro pendiente
        if (pending == null) {
            throw new BusinessException("No se encontró ningún registro pendiente o ya ha sido verificado");
        }

        // 2. Validar que el código no haya expirado
        if (pending.isExpired()) {
            pendingRegistrationService.remove(correo);
            throw new BusinessException("El código de verificación ha expirado");
        }

        // 3. Validar el código de verificación
        if (!pending.getCode().equals(codigo)) {
            throw new BusinessException("Código de verificación inválido");
        }

        // 4. Persistir al usuario en la base de datos
        RegisterRequestDto dto = pending.getRequestDto();
        LocalDateTime now = LocalDateTime.now(java.time.ZoneId.systemDefault());

        Usuario nuevoUsuario = Usuario.builder()
                .dni(dto.getDni())
                .nombres(dto.getNombres())
                .apellidos(dto.getApellidos())
                .correoInstitucional(dto.getCorreoInstitucional().trim())
                .telefono(dto.getTelefono())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .activo(true) // Se activa al verificar su correo
                .mustChangePassword(false) // Auto-registro no obliga a cambiar contraseña inicial
                .rolCodigo(dto.getRolCodigo())
                .fechaCreacion(now)
                .fechaActualizacion(now)
                .build();

        Usuario guardado = usuarioRepository.save(nuevoUsuario);

        // 5. Limpiar el registro pendiente en memoria
        pendingRegistrationService.remove(correo);

        // 6. Generar JWT para iniciar sesión de inmediato
        String token = tokenProvider.generateToken(guardado.getCorreoInstitucional(), guardado.getRolCodigo());

        return LoginResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .correo(guardado.getCorreoInstitucional())
                .nombres(guardado.getNombres())
                .apellidos(guardado.getApellidos())
                .rolCodigo(guardado.getRolCodigo())
                .mustChangePassword(guardado.isMustChangePassword())
                .requiresVerification(false)
                .build();
    }
}
