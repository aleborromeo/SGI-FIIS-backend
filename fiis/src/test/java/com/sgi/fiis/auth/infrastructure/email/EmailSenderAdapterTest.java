package com.sgi.fiis.auth.infrastructure.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailSenderAdapter Unit Tests")
class EmailSenderAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderAdapter emailSenderAdapter;

    @Test
    @DisplayName("Should successfully send verification code email")
    void sendVerificationCode_shouldSendCorrectMail() {
        ReflectionTestUtils.setField(emailSenderAdapter, "fromEmail", "test-sender@unas.edu.pe");

        String to = "user@unas.edu.pe";
        String code = "123456";

        emailSenderAdapter.sendVerificationCode(to, code);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("test-sender@unas.edu.pe", capturedMessage.getFrom());
        assertEquals("user@unas.edu.pe", capturedMessage.getTo()[0]);
        assertEquals("Código de Verificación - SGI FIIS", capturedMessage.getSubject());
        assertEquals("Tu código de verificación de 6 dígitos es: 123456\nEste código expira en 5 minutos.", capturedMessage.getText());
    }
}
