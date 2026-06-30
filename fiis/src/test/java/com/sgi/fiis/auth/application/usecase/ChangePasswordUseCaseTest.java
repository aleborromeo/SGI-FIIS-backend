package com.sgi.fiis.auth.application.usecase;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChangePasswordUseCase Unit Tests")
class ChangePasswordUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private ChangePasswordUseCase changePasswordUseCase;

    @Test
    @DisplayName("Should successfully change password when old password is correct")
    void testChangePasswordSuccess() {
        User user = User.builder()
                .institutionalEmail("admin@unas.edu.pe")
                .passwordHash("hashed-old-pass")
                .mustChangePassword(true)
                .build();

        when(userRepository.findByEmail("admin@unas.edu.pe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old-pass", "hashed-old-pass")).thenReturn(true);
        when(passwordEncoder.encode("new-pass")).thenReturn("hashed-new-pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        changePasswordUseCase.execute("admin@unas.edu.pe", "old-pass", "new-pass");

        assertEquals("hashed-new-pass", user.getPasswordHash());
        assertFalse(user.isMustChangePassword());

        verify(userRepository).findByEmail("admin@unas.edu.pe");
        verify(passwordEncoder).matches("old-pass", "hashed-old-pass");
        verify(passwordEncoder).encode("new-pass");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw BusinessException when old password does not match")
    void testChangePasswordIncorrect() {
        User user = User.builder()
                .institutionalEmail("admin@unas.edu.pe")
                .passwordHash("hashed-old-pass")
                .build();

        when(userRepository.findByEmail("admin@unas.edu.pe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-pass", "hashed-old-pass")).thenReturn(false);

        assertThrows(BusinessException.class, () ->
                changePasswordUseCase.execute("admin@unas.edu.pe", "wrong-pass", "new-pass"));

        verify(userRepository).findByEmail("admin@unas.edu.pe");
        verify(passwordEncoder).matches("wrong-pass", "hashed-old-pass");
        verifyNoMoreInteractions(passwordEncoder, userRepository);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not found")
    void testChangePasswordUserNotFound() {
        when(userRepository.findByEmail("invalid@unas.edu.pe")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                changePasswordUseCase.execute("invalid@unas.edu.pe", "old-pass", "new-pass"));

        verify(userRepository).findByEmail("invalid@unas.edu.pe");
        verifyNoInteractions(passwordEncoder);
        verifyNoMoreInteractions(userRepository);
    }
}
