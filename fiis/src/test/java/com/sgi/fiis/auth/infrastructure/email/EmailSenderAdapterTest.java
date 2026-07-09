package com.sgi.fiis.auth.infrastructure.email;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailSenderAdapter Unit Tests")
class EmailSenderAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderAdapter emailSenderAdapter;

    @Test
    @DisplayName("Should successfully send verification code email")
    void sendVerificationCode_shouldSendCorrectMail() throws Exception {
        ReflectionTestUtils.setField(emailSenderAdapter, "fromEmail", "test-sender@unas.edu.pe");

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String to = "user@unas.edu.pe";
        String code = "123456";

        emailSenderAdapter.sendVerificationCode(to, code);

        verify(mailSender).send(mimeMessage);

        assertEquals("Código de Verificación - SGI FIIS", mimeMessage.getSubject());
        assertEquals("test-sender@unas.edu.pe", mimeMessage.getFrom()[0].toString());
        assertEquals("user@unas.edu.pe", mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        
        // El contenido es HTML multipart, verificamos que tenga contenido
        Object content = mimeMessage.getContent();
        assertTrue(content instanceof jakarta.mail.internet.MimeMultipart);
    }

    @Test
    @DisplayName("Should successfully send password reset code email")
    void sendPasswordResetCode_shouldSendCorrectMail() throws Exception {
        ReflectionTestUtils.setField(emailSenderAdapter, "fromEmail", "test-sender@unas.edu.pe");

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        String to = "user@unas.edu.pe";
        String code = "654321";

        emailSenderAdapter.sendPasswordResetCode(to, code);

        verify(mailSender).send(mimeMessage);

        assertEquals("Restablecer Contraseña - SGI FIIS", mimeMessage.getSubject());
        assertEquals("test-sender@unas.edu.pe", mimeMessage.getFrom()[0].toString());
        assertEquals("user@unas.edu.pe", mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString());
        
        Object content = mimeMessage.getContent();
        assertTrue(content instanceof jakarta.mail.internet.MimeMultipart);
    }
}
