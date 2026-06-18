package com.sgi.fiis.auth.infrastructure.email;

import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Adaptador de infraestructura para enviar correos usando JavaMailSender.
 */
@Component
public class EmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailSenderAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Código de Verificación - SGI FIIS");
        message.setText("Tu código de verificación de 6 dígitos es: " + code + "\nEste código expira en 5 minutos.");
        mailSender.send(message);
    }
}
