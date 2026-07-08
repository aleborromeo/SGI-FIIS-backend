package com.sgi.fiis.config;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class MailMockConfigTest {

    @Test
    void init_shouldExecuteWithoutErrors() {
        MailMockConfig config = new MailMockConfig();
        assertDoesNotThrow(config::init);
    }

    @Test
    void javaMailSender_shouldReturnMockedInstance() {
        MailMockConfig config = new MailMockConfig();
        JavaMailSender mailSender = config.javaMailSender();
        
        assertNotNull(mailSender);
        
        // createMimeMessage() returns null in the mock
        assertNull(mailSender.createMimeMessage());
        assertNull(mailSender.createMimeMessage((InputStream) null));
        
        // send() operations should be no-op and not throw exceptions
        assertDoesNotThrow(() -> mailSender.send(new MimeMessage[0]));
        assertDoesNotThrow(() -> mailSender.send(new SimpleMailMessage[0]));
        
        MimeMessagePreparator preparator = mock(MimeMessagePreparator.class);
        assertDoesNotThrow(() -> mailSender.send(preparator));
    }
}
