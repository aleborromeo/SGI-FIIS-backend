package com.sgi.fiis.auth.domain.port;

import com.sgi.fiis.users.domain.model.Usuario;

/**
 * Puerto del dominio para generación y validación de tokens JWT.
 */
public interface TokenProviderPort {
    String generateToken(String email, String rolCodigo);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
}
