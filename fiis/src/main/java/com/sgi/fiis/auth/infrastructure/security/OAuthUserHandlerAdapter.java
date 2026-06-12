package com.sgi.fiis.auth.infrastructure.security;

import com.sgi.fiis.auth.domain.port.OAuthUserHandlerPort;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Adaptador que implementa OAuthUserHandlerPort.
 * Busca un usuario existente por correo institucional;
 * si no existe, crea uno nuevo con datos mínimos del proveedor OAuth.
 */
@Component
public class OAuthUserHandlerAdapter implements OAuthUserHandlerPort {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;

    public OAuthUserHandlerAdapter(UsuarioRepositoryPort usuarioRepository,
                                   PasswordEncoderPort passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario findOrCreateFromOAuth(String email, String name, String provider) {
        return usuarioRepository.findByCorreo(email)
                .orElseGet(() -> createOAuthUser(email, name, provider));
    }

    /**
     * Crea un usuario nuevo a partir de datos OAuth.
     * - DNI: placeholder "OA-" + UUID corto (máximo 8 caracteres)
     * - Password: UUID aleatorio hasheado (no se usará para login)
     * - Rol: ESTUDIANTE por defecto
     * - activo: true
     * - mustChangePassword: false (no aplica para OAuth)
     */
    private Usuario createOAuthUser(String email, String name, String provider) {
        // Generar DNI placeholder único (8 caracteres máximo por constraint de BD)
        String dniPlaceholder = "OA" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 6).toUpperCase();

        // Password placeholder hasheado (el usuario no la usará)
        String passwordPlaceholder = passwordEncoder.encode(UUID.randomUUID().toString());

        // Separar nombre y apellido del nombre completo
        String[] parts = name.trim().split("\\s+", 2);
        String nombres = parts[0];
        String apellidos = parts.length > 1 ? parts[1] : "";

        LocalDateTime now = LocalDateTime.now();

        Usuario nuevoUsuario = Usuario.builder()
                .dni(dniPlaceholder)
                .nombres(nombres)
                .apellidos(apellidos)
                .correoInstitucional(email)
                .passwordHash(passwordPlaceholder)
                .activo(true)
                .mustChangePassword(false)
                .rolCodigo("ESTUDIANTE")
                .oauthProvider(provider)
                .fechaCreacion(now)
                .fechaActualizacion(now)
                .build();

        return usuarioRepository.save(nuevoUsuario);
    }
}
