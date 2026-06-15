package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.BusinessException;
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
@DisplayName("CambiarEstadoUsuarioUseCase Unit Tests")
class CambiarEstadoUsuarioUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks
    private CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;

    @Test
    @DisplayName("Should successfully activate a user")
    void testActivarUsuarioExito() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .activo(false)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario result = cambiarEstadoUsuarioUseCase.execute(1L, true, 2L);

        assertNotNull(result);
        assertTrue(result.isActivo());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should successfully deactivate a user")
    void testDesactivarUsuarioExito() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .activo(true)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario result = cambiarEstadoUsuarioUseCase.execute(1L, false, 2L);

        assertNotNull(result);
        assertFalse(result.isActivo());

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should throw BusinessException when admin attempts to deactivate themselves")
    void testDesactivarAutodeactivacionThrows() {
        assertThrows(BusinessException.class, () -> 
                cambiarEstadoUsuarioUseCase.execute(1L, false, 1L));

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user is not found")
    void testCambiarEstadoUsuarioNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                cambiarEstadoUsuarioUseCase.execute(1L, true, 2L));

        verify(usuarioRepository).findById(1L);
        verifyNoMoreInteractions(usuarioRepository);
    }
}
