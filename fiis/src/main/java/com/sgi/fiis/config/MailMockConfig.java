package com.sgi.fiis.config;

import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;

/**
 * Configuración aislada que provee un Bean ficticio (mock) de {@link JavaMailSender}.
 * <p>
 * Su único propósito es satisfacer la dependencia de inyección requerida por
 * {@code com.sgi.fiis.auth.infrastructure.email.EmailSenderAdapter} durante
 * el arranque del contexto de Spring, sin modificar ningún archivo del módulo
 * de autenticación ni del módulo de usuarios.
 * <p>
 * Todas las operaciones de envío son no-operativas (no-op).
 */
@Configuration
@ConditionalOnProperty(name = "app.mail.mock", havingValue = "true", matchIfMissing = true)
@lombok.extern.slf4j.Slf4j
public class MailMockConfig {

    @PostConstruct
    public void init() {
        log.info("=================================================");
        log.info("[MAIL CONFIG] ¡ATENCIÓN! Modo MOCK de correo activo.");
        log.info("Los correos no se enviarán a bandejas reales.");
        log.info("=================================================");
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSender() {

            @Override
            public MimeMessage createMimeMessage() {
                return null;
            }

            @Override
            public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
                return null;
            }

            @Override
            public void send(MimeMessage... mimeMessages) throws MailException {
                // No-op: bean ficticio para satisfacer la dependencia del módulo auth.
            }

            @Override
            public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
                // No-op
            }

            @Override
            public void send(SimpleMailMessage... simpleMessages) throws MailException {
                // No-op
            }
        };
    }
}
