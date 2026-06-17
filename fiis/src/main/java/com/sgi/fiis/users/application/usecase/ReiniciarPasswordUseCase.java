package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Reiniciar contraseña de un usuario (RF-12).
 * La contraseña se restablece al DNI del usuario.
 */
@Service
public class ReiniciarPasswordUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    public ReiniciarPasswordUseCase(UsuarioRepositoryPort usuarioRepository,
                                     PasswordEncoderPort passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setPasswordHash(passwordEncoder.encode(usuario.getDni()));
        usuario.marcarCambioPasswordObligatorio();

        usuarioRepository.save(usuario);
    }
}
