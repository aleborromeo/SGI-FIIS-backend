package com.sgi.fiis.auth.domain.port;

/**
 * Puerto del dominio para codificación de contraseñas.
 * Abstrae la implementación de BCrypt o cualquier otro algoritmo.
 */
public interface PasswordEncoderPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
