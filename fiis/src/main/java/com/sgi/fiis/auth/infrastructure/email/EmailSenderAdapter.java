package com.sgi.fiis.auth.infrastructure.email;

import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

/**
 * Adaptador de infraestructura para enviar correos usando JavaMailSender con formato HTML.
 */
@Component
public class EmailSenderAdapter implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderAdapter.class);
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:sgi@nilver.store}")
    private String fromEmail;

    public EmailSenderAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        log.info("[SMTP] Intentando enviar correo");
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("[SMTP] Correo enviado exitosamente");
        } catch (Exception e) {
            log.error("[SMTP] Error crítico al enviar correo", e);
        }
    }

    @Override
    @Async
    public void sendVerificationCode(String to, String code) {
        String subject = "Código de Verificación - SGI FIIS";
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset=\"UTF-8\"><title>Código de Verificación</title></head>"
                + "<body style=\"margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #334155;\">"
                + "  <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); overflow: hidden; border: 1px solid #e2e8f0;\">"
                + "    <tr>"
                + "      <td style=\"background-color: #1a365d; padding: 30px 20px; text-align: center; border-bottom: 4px solid #2563eb;\">"
                + "        <h1 style=\"margin: 0; color: #ffffff; font-size: 22px; font-weight: 700; letter-spacing: 0.5px;\">SGI - FIIS</h1>"
                + "        <p style=\"margin: 5px 0 0 0; color: #93c5fd; font-size: 14px;\">Sistema de Gestión de Investigación</p>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style=\"padding: 40px 30px;\">"
                + "        <h2 style=\"margin: 0 0 16px 0; color: #1e293b; font-size: 18px; font-weight: 600;\">Código de Verificación de Registro</h2>"
                + "        <p style=\"margin: 0 0 24px 0; font-size: 15px; line-height: 1.6; color: #475569;\">Hola,</p>"
                + "        <p style=\"margin: 0 0 24px 0; font-size: 15px; line-height: 1.6; color: #475569;\">Gracias por registrarte en el Sistema de Gestión de Investigación. Usa el siguiente código de verificación de 6 dígitos para activar tu cuenta:</p>"
                + "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 30px auto; background-color: #f0f7ff; border-radius: 8px; border: 1px dashed #2563eb;\">"
                + "          <tr>"
                + "            <td style=\"padding: 15px 40px; font-size: 32px; font-weight: 700; letter-spacing: 6px; color: #2563eb; text-align: center;\">" + code + "</td>"
                + "          </tr>"
                + "        </table>"
                + "        <p style=\"margin: 0 0 24px 0; font-size: 14px; line-height: 1.6; color: #64748b; text-align: center;\"><strong>Importante:</strong> Este código es temporal y <strong>expira en 5 minutos</strong>.</p>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style=\"background-color: #f1f5f9; padding: 20px; text-align: center; border-top: 1px solid #e2e8f0;\">"
                + "        <p style=\"margin: 0; font-size: 12px; color: #94a3b8; line-height: 1.5;\">Universidad Nacional Agraria de la Selva<br>Facultad de Ingeniería en Informática y Sistemas (FIIS)<br>© 2026 SGI. Todos los derechos reservados.</p>"
                + "      </td>"
                + "    </tr>"
                + "  </table>"
                + "</body>"
                + "</html>";
        sendHtmlEmail(to, subject, htmlContent);
    }

    @Override
    @Async
    public void sendPasswordResetCode(String to, String code) {
        String subject = "Restablecer Contraseña - SGI FIIS";
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset=\"UTF-8\"><title>Restablecer Contraseña</title></head>"
                + "<body style=\"margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #334155;\">"
                + "  <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); overflow: hidden; border: 1px solid #e2e8f0;\">"
                + "    <tr>"
                + "      <td style=\"background-color: #1a365d; padding: 30px 20px; text-align: center; border-bottom: 4px solid #2563eb;\">"
                + "        <h1 style=\"margin: 0; color: #ffffff; font-size: 22px; font-weight: 700; letter-spacing: 0.5px;\">SGI - FIIS</h1>"
                + "        <p style=\"margin: 5px 0 0 0; color: #93c5fd; font-size: 14px;\">Sistema de Gestión de Investigación</p>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style=\"padding: 40px 30px;\">"
                + "        <h2 style=\"margin: 0 0 16px 0; color: #1e293b; font-size: 18px; font-weight: 600;\">Solicitud de Restablecimiento de Contraseña</h2>"
                + "        <p style=\"margin: 0 0 24px 0; font-size: 15px; line-height: 1.6; color: #475569;\">Hola,</p>"
                + "        <p style=\"margin: 0 0 24px 0; font-size: 15px; line-height: 1.6; color: #475569;\">Hemos recibido una solicitud para restablecer la contraseña de tu cuenta institucional. Usa el siguiente código de verificación de 6 dígitos para completar el proceso:</p>"
                + "        <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin: 30px auto; background-color: #f0f7ff; border-radius: 8px; border: 1px dashed #2563eb;\">"
                + "          <tr>"
                + "            <td style=\"padding: 15px 40px; font-size: 32px; font-weight: 700; letter-spacing: 6px; color: #2563eb; text-align: center;\">" + code + "</td>"
                + "          </tr>"
                + "        </table>"
                + "        <p style=\"margin: 0 0 24px 0; font-size: 14px; line-height: 1.6; color: #64748b; text-align: center;\"><strong>Importante:</strong> Este código es temporal y <strong>expira en 5 minutos</strong>. Si no solicitaste este cambio, puedes ignorar este mensaje de forma segura.</p>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style=\"background-color: #f1f5f9; padding: 20px; text-align: center; border-top: 1px solid #e2e8f0;\">"
                + "        <p style=\"margin: 0; font-size: 12px; color: #94a3b8; line-height: 1.5;\">Universidad Nacional Agraria de la Selva<br>Facultad de Ingeniería en Informática y Sistemas (FIIS)<br>© 2026 SGI. Todos los derechos reservados.</p>"
                + "      </td>"
                + "    </tr>"
                + "  </table>"
                + "</body>"
                + "</html>";
        sendHtmlEmail(to, subject, htmlContent);
    }

    @Override
    @Async
    public void sendNewUserCredentials(String to, String rawPassword) {
        String subject = "Bienvenido a SGI FIIS - Tus Credenciales de Acceso";
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset=\"UTF-8\"><title>Bienvenido a SGI FIIS</title></head>"
                + "<body style=\"margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #f8fafc; color: #334155;\">"
                + "  <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); overflow: hidden; border: 1px solid #e2e8f0;\">"
                + "    <tr>"
                + "      <td style=\"background-color: #1a365d; padding: 30px 20px; text-align: center; border-bottom: 4px solid #2563eb;\">"
                + "        <h1 style=\"margin: 0; color: #ffffff; font-size: 22px; font-weight: 700; letter-spacing: 0.5px;\">SGI - FIIS</h1>"
                + "        <p style=\"margin: 5px 0 0 0; color: #93c5fd; font-size: 14px;\">Sistema de Gestión de Investigación</p>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style=\"padding: 40px 30px;\">"
                + "        <h2 style=\"margin: 0 0 16px 0; color: #1e293b; font-size: 18px; font-weight: 600;\">¡Bienvenido al Sistema!</h2>"
                + "        <p style=\"margin: 0 0 16px 0; font-size: 15px; line-height: 1.6; color: #475569;\">Hola,</p>"
                + "        <p style=\"margin: 0 0 24px 0; font-size: 15px; line-height: 1.6; color: #475569;\">Tu cuenta ha sido creada en el Sistema de Gestión de Investigación de la FIIS. A continuación, te brindamos tus credenciales de acceso temporal:</p>"
                + "        <table border=\"0\" cellpadding=\"10\" cellspacing=\"0\" width=\"100%\" style=\"margin: 20px 0; background-color: #f8fafc; border-radius: 8px; border: 1px solid #e2e8f0; font-size: 15px;\">"
                + "          <tr>"
                + "            <td width=\"30%\" style=\"color: #64748b; font-weight: 600;\">Usuario:</td>"
                + "            <td style=\"color: #1e293b; font-weight: 700;\">" + to + "</td>"
                + "          </tr>"
                + "          <tr>"
                + "            <td style=\"color: #64748b; font-weight: 600;\">Contraseña:</td>"
                + "            <td style=\"color: #2563eb; font-family: monospace; font-weight: 700; font-size: 16px;\">" + rawPassword + "</td>"
                + "          </tr>"
                + "        </table>"
                + "        <p style=\"margin: 24px 0 16px 0; font-size: 14px; line-height: 1.6; color: #ef4444;\"><strong>Importante:</strong> Por motivos de seguridad, el sistema te solicitará cambiar esta contraseña temporal obligatoriamente en tu primer inicio de sesión.</p>"
                + "        <div style=\"text-align: center; margin: 30px 0;\">"
                + "          <a href=\"http://localhost:5173/login\" style=\"background-color: #2563eb; color: #ffffff; padding: 12px 30px; border-radius: 6px; text-decoration: none; font-weight: 600; font-size: 15px; display: inline-block;\">Iniciar Sesión</a>"
                + "        </div>"
                + "      </td>"
                + "    </tr>"
                + "    <tr>"
                + "      <td style=\"background-color: #f1f5f9; padding: 20px; text-align: center; border-top: 1px solid #e2e8f0;\">"
                + "        <p style=\"margin: 0; font-size: 12px; color: #94a3b8; line-height: 1.5;\">Universidad Nacional Agraria de la Selva<br>Facultad de Ingeniería en Informática y Sistemas (FIIS)<br>© 2026 SGI. Todos los derechos reservados.</p>"
                + "      </td>"
                + "    </tr>"
                + "  </table>"
                + "</body>"
                + "</html>";
        sendHtmlEmail(to, subject, htmlContent);
    }
}
