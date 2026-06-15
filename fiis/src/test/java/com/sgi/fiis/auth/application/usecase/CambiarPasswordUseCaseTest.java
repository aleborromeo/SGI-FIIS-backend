package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
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
@DisplayName("CambiarPasswordUseCase Unit Tests")
class CambiarPasswordUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private CambiarPasswordUseCase cambiarPasswordUseCase;

    @Test
    @DisplayName("Should successfully change password when old password is correct")
    void testCambiarPasswordExito() {
        Usuario usuario = Usuario.builder()
                .correoInstitucional("admin@unas.edu.pe")
                .passwordHash("hashed-old-pass")
                .mustChangePassword(true)
                .build();

        when(usuarioRepository.findByCorreo("admin@unas.edu.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("old-pass", "hashed-old-pass")).thenReturn(true);
        when(passwordEncoder.encode("new-pass")).thenReturn("hashed-new-pass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        cambiarPasswordUseCase.execute("admin@unas.edu.pe", "old-pass", "new-pass");

        assertEquals("hashed-new-pass", usuario.getPasswordHash());
        assertFalse(usuario.isMustChangePassword());

        verify(usuarioRepository).findByCorreo("admin@unas.edu.pe");
        verify(passwordEncoder).matches("old-pass", "hashed-old-pass");
        verify(passwordEncoder).encode("new-pass");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should throw BusinessException when old password does not match")
    void testCambiarPasswordIncorrecta() {
        Usuario usuario = Usuario.builder()
                .correoInstitucional("admin@unas.edu.pe")
                .passwordHash("hashed-old-pass")
                .build();

        when(usuarioRepository.findByCorreo("admin@unas.edu.pe")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong-pass", "hashed-old-pass")).thenReturn(false);

        assertThrows(BusinessException.class, () -> 
                cambiarPasswordUseCase.execute("admin@unas.edu.pe", "wrong-pass", "new-pass"));

        verify(usuarioRepository).findByCorreo("admin@unas.edu.pe");
        verify(passwordEncoder).matches("wrong-pass", "hashed-old-pass");
        verifyNoMoreInteractions(passwordEncoder, usuarioRepository);
    }

    @Test
    @DisplayName("Should throw BusinessException when user is not found")
    void testCambiarPasswordUsuarioNotFound() {
        when(usuarioRepository.findByCorreo("invalid@unas.edu.pe")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> 
                cambiarPasswordUseCase.execute("invalid@unas.edu.pe", "old-pass", "new-pass"));

        verify(usuarioRepository).findByCorreo("invalid@unas.edu.pe");
        verifyNoInteractions(passwordEncoder);
        verifyNoMoreInteractions(usuarioRepository);
    }
}
