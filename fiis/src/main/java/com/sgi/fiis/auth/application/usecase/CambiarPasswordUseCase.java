package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Cambiar contraseña del usuario autenticado (RF-06).
 */
@Service
public class CambiarPasswordUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    public CambiarPasswordUseCase(UsuarioRepositoryPort usuarioRepository,
                                   PasswordEncoderPort passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(String correoUsuario, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByCorreo(correoUsuario)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Validar contraseña actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        // Actualizar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(passwordNueva));
        usuario.confirmarCambioPassword();

        usuarioRepository.save(usuario);
    }
}
