package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.service.PendingResetPasswordService;
import com.sgi.fiis.auth.application.service.PendingResetPasswordService.ResetPasswordCode;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SelfResetPasswordUseCase Unit Tests")
class SelfResetPasswordUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private PendingResetPasswordService pendingResetPasswordService;

    @InjectMocks
    private SelfResetPasswordUseCase selfResetPasswordUseCase;

    @Test
    @DisplayName("Should successfully reset password when all validations pass")
    void testExecuteSuccess() {
        String email = "user@unas.edu.pe";
        String code = "123456";
        String newPassword = "NewPassword123!";
        
        ResetPasswordCode mockResetCode = mock(ResetPasswordCode.class);
        when(mockResetCode.getCode()).thenReturn(code);
        when(mockResetCode.isExpired()).thenReturn(false);
        when(pendingResetPasswordService.get(email)).thenReturn(mockResetCode);

        User mockUser = new User();
        mockUser.setInstitutionalEmail(email);
        mockUser.setMustChangePassword(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        when(passwordEncoder.encode(newPassword)).thenReturn("encodedHash");

        selfResetPasswordUseCase.execute(email, code, newPassword, newPassword);

        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(mockUser);
        verify(pendingResetPasswordService).remove(email);
        
        assertEquals("encodedHash", mockUser.getPasswordHash());
        assertFalse(mockUser.isMustChangePassword());
    }

    @Test
    @DisplayName("Should throw BusinessException when passwords mismatch")
    void testExecutePasswordMismatch() {
        assertThrows(BusinessException.class, () -> {
            selfResetPasswordUseCase.execute("user@unas.edu.pe", "123456", "Pass1", "Pass2");
        }, "auth.password.mismatch");

        verifyNoInteractions(pendingResetPasswordService, userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw BusinessException when reset code is not found")
    void testExecuteCodeNotFound() {
        String email = "user@unas.edu.pe";
        when(pendingResetPasswordService.get(email)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            selfResetPasswordUseCase.execute(email, "123456", "Pass123!", "Pass123!");
        });

        assertEquals("auth.code.invalid", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException and remove code when code is expired")
    void testExecuteCodeExpired() {
        String email = "user@unas.edu.pe";
        ResetPasswordCode mockResetCode = mock(ResetPasswordCode.class);
        when(mockResetCode.isExpired()).thenReturn(true);
        when(pendingResetPasswordService.get(email)).thenReturn(mockResetCode);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            selfResetPasswordUseCase.execute(email, "123456", "Pass123!", "Pass123!");
        });

        assertEquals("auth.code.expired", exception.getMessage());
        verify(pendingResetPasswordService).remove(email);
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when codes mismatch")
    void testExecuteCodeMismatch() {
        String email = "user@unas.edu.pe";
        ResetPasswordCode mockResetCode = mock(ResetPasswordCode.class);
        when(mockResetCode.isExpired()).thenReturn(false);
        when(mockResetCode.getCode()).thenReturn("123456");
        when(pendingResetPasswordService.get(email)).thenReturn(mockResetCode);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            selfResetPasswordUseCase.execute(email, "incorrect_code", "Pass123!", "Pass123!");
        });

        assertEquals("auth.code.invalid", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not found")
    void testExecuteUserNotFound() {
        String email = "user@unas.edu.pe";
        String code = "123456";
        ResetPasswordCode mockResetCode = mock(ResetPasswordCode.class);
        when(mockResetCode.getCode()).thenReturn(code);
        when(mockResetCode.isExpired()).thenReturn(false);
        when(pendingResetPasswordService.get(email)).thenReturn(mockResetCode);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            selfResetPasswordUseCase.execute(email, code, "Pass123!", "Pass123!");
        });

        assertEquals("auth.user.not-found", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any());
    }
}
