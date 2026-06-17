package com.sgi.fiis.users.application.usecase;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObtenerUsuarioUseCase Unit Tests")
class ObtenerUsuarioUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks
    private ObtenerUsuarioUseCase obtenerUsuarioUseCase;

    @Test
    @DisplayName("Should successfully return user by id")
    void testObtenerUsuarioExito() {
        Usuario usuario = Usuario.builder().id(1L).nombres("Juan").build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario result = obtenerUsuarioUseCase.execute(1L);
        assertNotNull(result);
        assertEquals("Juan", result.getNombres());

        verify(usuarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user by id does not exist")
    void testObtenerUsuarioNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> obtenerUsuarioUseCase.execute(1L));

        verify(usuarioRepository).findById(1L);
    }
}
