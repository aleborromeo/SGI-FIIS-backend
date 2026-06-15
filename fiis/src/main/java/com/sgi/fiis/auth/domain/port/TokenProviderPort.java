package com.sgi.fiis.auth.domain.port;


/**
 * Puerto del dominio para generación y validación de tokens JWT.
 */
public interface TokenProviderPort {
    String generateToken(String email, String rolCodigo);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
}
