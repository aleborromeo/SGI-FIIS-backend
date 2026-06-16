package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.RolRepositoryPort;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Caso de uso: Crear usuario (RF-07).
 * - Genera correo institucional automáticamente si no se proporciona (RF-10).
 * - La contraseña inicial es el DNI del usuario, hasheada con BCrypt.
 */
@Service
public class CrearUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final RolRepositoryPort rolRepository;
    private final PasswordEncoderPort passwordEncoder;

    public CrearUsuarioUseCase(UsuarioRepositoryPort usuarioRepository,
                               RolRepositoryPort rolRepository,
                               PasswordEncoderPort passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario execute(Usuario usuario) {
        // Validar que el rol exista
        rolRepository.findByCodigo(usuario.getRolCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "codigo", usuario.getRolCodigo()));

        // Validar duplicidad de DNI (RNF-38)
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            throw new DuplicateResourceException("Usuario", "DNI", usuario.getDni());
        }

        // Generar correo institucional si no se proporcionó (RF-10)
        usuario.generarCorreoInstitucional();

        // Validar que el correo institucional termine con .edu.pe
        if (usuario.getCorreoInstitucional() == null || !usuario.getCorreoInstitucional().toLowerCase().endsWith(".edu.pe")) {
            throw new BusinessException("El correo institucional debe pertenecer al dominio .edu.pe");
        }

        // Validar duplicidad de correo (RNF-38)
        if (usuarioRepository.existsByCorreo(usuario.getCorreoInstitucional())) {
            throw new DuplicateResourceException("Usuario", "correo", usuario.getCorreoInstitucional());
        }

        // Contraseña inicial = DNI hasheado
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getDni()));
        usuario.setActivo(true);
        usuario.setMustChangePassword(true);
        usuario.setFechaCreacion(LocalDateTime.now(java.time.ZoneId.systemDefault()));
        usuario.setFechaActualizacion(LocalDateTime.now(java.time.ZoneId.systemDefault()));

        return usuarioRepository.save(usuario);
    }
}
