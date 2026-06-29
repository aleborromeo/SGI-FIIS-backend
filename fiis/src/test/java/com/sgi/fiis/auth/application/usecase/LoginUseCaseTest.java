package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase Unit Tests")
class LoginUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private TokenProviderPort tokenProvider;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    @DisplayName("Should successfully login credentials, generate and return JWT directly")
    void testLoginSuccess() {
        User user = User.builder()
                .institutionalEmail("admin@unas.edu.pe")
                .firstNames("Admin")
                .lastNames("Sistema")
                .active(true)
                .mustChangePassword(true)
                .passwordHash("hashed-pass")
                .roleCode("ADMIN")
                .build();

        when(userRepository.findByEmail("admin@unas.edu.pe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("00000000", "hashed-pass")).thenReturn(true);
        when(tokenProvider.generateToken("admin@unas.edu.pe", "ADMIN")).thenReturn("jwt-token");

        LoginResponseDto response = loginUseCase.execute("admin@unas.edu.pe", "00000000");

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("admin@unas.edu.pe", response.getEmail());
        assertFalse(response.isRequiresVerification());

        verify(userRepository).findByEmail("admin@unas.edu.pe");
        verify(passwordEncoder).matches("00000000", "hashed-pass");
        verify(tokenProvider).generateToken("admin@unas.edu.pe", "ADMIN");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user not found")
    void testLoginUserNotFound() {
        when(userRepository.findByEmail("invalid@unas.edu.pe")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () ->
                loginUseCase.execute("invalid@unas.edu.pe", "password"));

        verify(userRepository).findByEmail("invalid@unas.edu.pe");
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is inactive")
    void testLoginInactiveUser() {
        User user = User.builder()
                .institutionalEmail("inactive@unas.edu.pe")
                .active(false)
                .build();

        when(userRepository.findByEmail("inactive@unas.edu.pe")).thenReturn(Optional.of(user));

        assertThrows(BusinessException.class, () ->
                loginUseCase.execute("inactive@unas.edu.pe", "password"));

        verify(userRepository).findByEmail("inactive@unas.edu.pe");
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password does not match")
    void testLoginPasswordMismatch() {
        User user = User.builder()
                .institutionalEmail("admin@unas.edu.pe")
                .active(true)
                .passwordHash("hashed-pass")
                .build();

        when(userRepository.findByEmail("admin@unas.edu.pe")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-pass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () ->
                loginUseCase.execute("admin@unas.edu.pe", "wrong-password"));

        verify(userRepository).findByEmail("admin@unas.edu.pe");
        verify(passwordEncoder).matches("wrong-password", "hashed-pass");
        verifyNoInteractions(tokenProvider);
    }

    @Test
    @DisplayName("Should handle null email gracefully by throwing BadCredentialsException")
    void testLoginNullEmail() {
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () ->
                loginUseCase.execute(null, "password"));

        verify(userRepository).findByEmail("");
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user password hash is null (OAuth user)")
    void testLoginNullPasswordHash() {
        User user = User.builder()
                .institutionalEmail("oauth@unas.edu.pe")
                .active(true)
                .passwordHash(null)
                .build();

        when(userRepository.findByEmail("oauth@unas.edu.pe")).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class, () ->
                loginUseCase.execute("oauth@unas.edu.pe", "password"));

        verify(userRepository).findByEmail("oauth@unas.edu.pe");
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }
}
