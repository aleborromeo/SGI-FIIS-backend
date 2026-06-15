package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.users.domain.model.Rol;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.RolRepositoryPort;
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
@DisplayName("EditarUsuarioUseCase Unit Tests")
class EditarUsuarioUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private RolRepositoryPort rolRepository;

    @InjectMocks
    private EditarUsuarioUseCase editarUsuarioUseCase;

    @Test
    @DisplayName("Should successfully update user fields when valid")
    void testEditarUsuarioExito() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Perez")
                .correoInstitucional("juan.perez@unas.edu.pe")
                .rolCodigo("ESTUDIANTE")
                .build();

        Rol rol = Rol.builder().codigoRol("DOCENTE_INVESTIGADOR").build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findByCodigo("DOCENTE_INVESTIGADOR")).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario result = editarUsuarioUseCase.execute(1L, "Juan Carlos", "Perez Gomez", 
                "jc.perez@unas.edu.pe", "999888777", "DOCENTE_INVESTIGADOR");

        assertNotNull(result);
        assertEquals("Juan Carlos", result.getNombres());
        assertEquals("Perez Gomez", result.getApellidos());
        assertEquals("jc.perez@unas.edu.pe", result.getCorreoInstitucional());
        assertEquals("999888777", result.getTelefono());
        assertEquals("DOCENTE_INVESTIGADOR", result.getRolCodigo());

        verify(usuarioRepository).findById(1L);
        verify(rolRepository).findByCodigo("DOCENTE_INVESTIGADOR");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user to edit does not exist")
    void testEditarUsuarioNoExiste() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
                editarUsuarioUseCase.execute(1L, "Juan", "Perez", null, null, null));

        verify(usuarioRepository).findById(1L);
        verifyNoInteractions(rolRepository);
        verifyNoMoreInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when new email belongs to another user")
    void testEditarUsuarioCorreoDuplicado() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .correoInstitucional("juan.perez@unas.edu.pe")
                .build();

        Usuario otroUsuario = Usuario.builder()
                .id(2L)
                .correoInstitucional("duplicado@unas.edu.pe")
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCorreo("duplicado@unas.edu.pe")).thenReturn(Optional.of(otroUsuario));

        assertThrows(DuplicateResourceException.class, () -> 
                editarUsuarioUseCase.execute(1L, null, null, "duplicado@unas.edu.pe", null, null));

        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).findByCorreo("duplicado@unas.edu.pe");
        verifyNoInteractions(rolRepository);
    }
}
