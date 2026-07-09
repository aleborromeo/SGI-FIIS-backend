package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.service.PendingResetPasswordService;
import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ForgotPasswordUseCase Unit Tests")
class ForgotPasswordUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PendingResetPasswordService pendingResetPasswordService;

    @Mock
    private EmailSenderPort emailSender;

    @InjectMocks
    private ForgotPasswordUseCase forgotPasswordUseCase;

    @Test
    @DisplayName("Should successfully generate code, register and send email when user exists")
    void testExecuteSuccess() {
        String email = "existing@unas.edu.pe";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        forgotPasswordUseCase.execute(email);

        verify(userRepository).existsByEmail(email);
        verify(pendingResetPasswordService).register(eq(email), anyString());
        verify(emailSender).sendPasswordResetCode(eq(email), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when user does not exist")
    void testExecuteUserNotFound() {
        String email = "notfound@unas.edu.pe";
        when(userRepository.existsByEmail(email)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            forgotPasswordUseCase.execute(email);
        });

        assertEquals("auth.user.not-found", exception.getMessage());
        verify(pendingResetPasswordService, never()).register(anyString(), anyString());
        verify(emailSender, never()).sendPasswordResetCode(anyString(), anyString());
    }
}
