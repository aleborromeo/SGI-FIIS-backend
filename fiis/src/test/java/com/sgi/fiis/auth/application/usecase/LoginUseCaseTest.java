package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
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
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @Mock
    private TokenProviderPort tokenProvider;

    @InjectMocks
    private LoginUseCase loginUseCase;

    @Test
    @DisplayName("Should successfully login credentials, generate and return JWT directly")
    void testLoginSuccess() {
        Usuario usuario = Usuario.builder()
                .correoInstitucional("admin@unas.edu.pe")
                .nombres("Admin")
                .apellidos("Sistema")
                .activo(true)
                .mustChangePassword(true)
                .passwordHash("hashed-pass")
                .rolCodigo("ADMIN")
                .build();

        when(usuarioRepository.findByCorreo("admin@unas.edu.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("00000000", "hashed-pass")).thenReturn(true);
        when(tokenProvider.generateToken("admin@unas.edu.pe", "ADMIN")).thenReturn("jwt-token");

        LoginResponseDto response = loginUseCase.execute("admin@unas.edu.pe", "00000000");

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals("admin@unas.edu.pe", response.getCorreo());
        assertFalse(response.isRequiresVerification());

        verify(usuarioRepository).findByCorreo("admin@unas.edu.pe");
        verify(passwordEncoder).matches("00000000", "hashed-pass");
        verify(tokenProvider).generateToken("admin@unas.edu.pe", "ADMIN");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user not found")
    void testLoginUserNotFound() {
        when(usuarioRepository.findByCorreo("invalid@unas.edu.pe")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> 
                loginUseCase.execute("invalid@unas.edu.pe", "password"));

        verify(usuarioRepository).findByCorreo("invalid@unas.edu.pe");
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is inactive")
    void testLoginInactiveUser() {
        Usuario usuario = Usuario.builder()
                .correoInstitucional("inactive@unas.edu.pe")
                .activo(false)
                .build();

        when(usuarioRepository.findByCorreo("inactive@unas.edu.pe")).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class, () -> 
                loginUseCase.execute("inactive@unas.edu.pe", "password"));

        verify(usuarioRepository).findByCorreo("inactive@unas.edu.pe");
        verifyNoInteractions(passwordEncoder, tokenProvider);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password does not match")
    void testLoginPasswordMismatch() {
        Usuario usuario = Usuario.builder()
                .correoInstitucional("admin@unas.edu.pe")
                .activo(true)
                .passwordHash("hashed-pass")
                .build();

        when(usuarioRepository.findByCorreo("admin@unas.edu.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong-password", "hashed-pass")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> 
                loginUseCase.execute("admin@unas.edu.pe", "wrong-password"));

        verify(usuarioRepository).findByCorreo("admin@unas.edu.pe");
        verify(passwordEncoder).matches("wrong-password", "hashed-pass");
        verifyNoInteractions(tokenProvider);
    }
}
