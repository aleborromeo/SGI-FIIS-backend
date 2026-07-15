package com.sgi.fiis.auth.domain.port;

/**
 * Puerto de dominio para enviar correos electrónicos.
 */
public interface EmailSenderPort {
    /**
     * Envía un código de verificación de 6 dígitos al correo especificado.
     *
     * @param to   correo destinatario
     * @param code código de verificación
     */
    void sendVerificationCode(String to, String code);

    /**
     * Envía un código de 6 dígitos para restablecer la contraseña al correo especificado.
     *
     * @param to   correo destinatario
     * @param code código de restablecimiento
     */
    void sendPasswordResetCode(String to, String code);

    /**
     * Envía las credenciales de acceso (usuario y contraseña generada) a un nuevo usuario.
     *
     * @param to          correo institucional destinatario
     * @param rawPassword contraseña temporal generada
     */
    void sendNewUserCredentials(String to, String rawPassword);
}
