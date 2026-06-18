package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Login (RF-01, RF-02).
 * Valida credenciales, verifica usuario activo y genera JWT.
 */
@Service
public class LoginUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginUseCase(UsuarioRepositoryPort usuarioRepository,
                        PasswordEncoderPort passwordEncoder,
                        TokenProviderPort tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponseDto execute(String correo, String password) {
        // Buscar usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        // RF-02: Validar que el usuario esté activo
        if (!usuario.isActivo()) {
            throw new BusinessException("Usuario inactivo. Contacte al administrador.");
        }

        // Validar contraseña
        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        // Generar JWT
        String token = tokenProvider.generateToken(usuario.getCorreoInstitucional(), usuario.getRolCodigo());

        return LoginResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .correo(usuario.getCorreoInstitucional())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .rolCodigo(usuario.getRolCodigo())
                .mustChangePassword(usuario.isMustChangePassword())
                .build();
    }
}
