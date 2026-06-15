package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
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
@DisplayName("ReiniciarPasswordUseCase Unit Tests")
class ReiniciarPasswordUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private ReiniciarPasswordUseCase reiniciarPasswordUseCase;

    @Test
    @DisplayName("Should successfully reset password to user DNI")
    void testReiniciarPasswordExito() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .dni("87654321")
                .passwordHash("old-hash")
                .mustChangePassword(false)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("87654321")).thenReturn("new-dni-hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reiniciarPasswordUseCase.execute(1L);

        assertEquals("new-dni-hash", usuario.getPasswordHash());
        assertTrue(usuario.isMustChangePassword());

        verify(usuarioRepository).findById(1L);
        verify(passwordEncoder).encode("87654321");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user does not exist")
    void testReiniciarPasswordNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reiniciarPasswordUseCase.execute(1L));

        verify(usuarioRepository).findById(1L);
        verifyNoInteractions(passwordEncoder);
        verifyNoMoreInteractions(usuarioRepository);
    }
}
