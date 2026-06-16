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
}
