package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListarUsuariosUseCase Unit Tests")
class ListarUsuariosUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks
    private ListarUsuariosUseCase listarUsuariosUseCase;

    @Test
    @DisplayName("Should call findAll when query is null or blank")
    void testListarTodos() {
        Usuario usuario = Usuario.builder().id(1L).nombres("Juan").build();
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(usuario));

        List<Usuario> result = listarUsuariosUseCase.execute(null);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getNombres());

        List<Usuario> resultBlank = listarUsuariosUseCase.execute("   ");
        assertEquals(1, resultBlank.size());

        verify(usuarioRepository, times(2)).findAll();
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Should call search with trimmed query when query is valid")
    void testBuscarConQuery() {
        Usuario usuario = Usuario.builder().id(1L).nombres("Juan").build();
        when(usuarioRepository.search("Juan")).thenReturn(Collections.singletonList(usuario));

        List<Usuario> result = listarUsuariosUseCase.execute(" Juan  ");
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getNombres());

        verify(usuarioRepository).search("Juan");
        verifyNoMoreInteractions(usuarioRepository);
    }
}
