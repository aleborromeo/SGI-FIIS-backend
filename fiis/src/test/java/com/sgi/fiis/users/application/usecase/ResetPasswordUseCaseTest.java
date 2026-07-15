package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResetPasswordUseCase Unit Tests")
class ResetPasswordUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private EmailSenderPort emailSender;

    @InjectMocks
    private ResetPasswordUseCase resetPasswordUseCase;

    @Test
    @DisplayName("Should successfully reset password with secure random password and send email")
    void testResetPasswordSuccess() {
        User user = User.builder()
                .id(1L)
                .dni("87654321")
                .institutionalEmail("test.user@unas.edu.pe")
                .passwordHash("old-hash")
                .mustChangePassword(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("new-secure-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        resetPasswordUseCase.execute(1L);

        assertEquals("new-secure-hash", user.getPasswordHash());
        assertTrue(user.isMustChangePassword());

        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(user);
        verify(emailSender).sendNewUserCredentials(eq("test.user@unas.edu.pe"), anyString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void testResetPasswordNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resetPasswordUseCase.execute(1L));

        verify(userRepository).findById(1L);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(emailSender);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Should successfully reset password even if email sender fails")
    void testResetPasswordMailSenderException() {
        User user = User.builder()
                .id(1L)
                .dni("87654321")
                .institutionalEmail("test.user@unas.edu.pe")
                .passwordHash("old-hash")
                .mustChangePassword(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("new-secure-hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doThrow(new RuntimeException("Mail server down")).when(emailSender).sendNewUserCredentials(anyString(), anyString());

        assertDoesNotThrow(() -> resetPasswordUseCase.execute(1L));
    }
}

