package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.domain.port.OAuthUserHandlerPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.users.domain.model.Usuario;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: Login vía OAuth2 (Microsoft).
 * Busca o crea el usuario a partir de los datos del proveedor OAuth,
 * y genera un JWT propio del sistema.
 */
@Service
public class OAuthLoginUseCase {

    private final OAuthUserHandlerPort oAuthUserHandler;
    private final TokenProviderPort tokenProvider;

    public OAuthLoginUseCase(OAuthUserHandlerPort oAuthUserHandler,
                             TokenProviderPort tokenProvider) {
        this.oAuthUserHandler = oAuthUserHandler;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Ejecuta el flujo OAuth: busca/crea usuario y genera JWT.
     *
     * @param email    correo del usuario desde el proveedor OAuth
     * @param name     nombre completo del usuario
     * @param provider nombre del proveedor (ej: "microsoft")
     * @return LoginResponseDto con el token JWT y datos del usuario
     */
    public LoginResponseDto execute(String email, String name, String provider) {
        Usuario usuario = oAuthUserHandler.findOrCreateFromOAuth(email, name, provider);

        String token = tokenProvider.generateToken(
                usuario.getCorreoInstitucional(),
                usuario.getRolCodigo()
        );

        return LoginResponseDto.builder()
                .token(token)
                .tipo("Bearer")
                .correo(usuario.getCorreoInstitucional())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .rolCodigo(usuario.getRolCodigo())
                .mustChangePassword(false) // Usuarios OAuth no necesitan cambiar contraseña
                .build();
    }
}
