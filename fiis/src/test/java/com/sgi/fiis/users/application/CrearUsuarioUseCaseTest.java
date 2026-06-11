package com.sgi.fiis.users.application.usecase;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
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
@DisplayName("CrearUsuarioUseCase Unit Tests")
class CrearUsuarioUseCaseTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private RolRepositoryPort rolRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private CrearUsuarioUseCase crearUsuarioUseCase;

    @Test
    @DisplayName("Should successfully create a new user and hash their password using DNI")
    void testCrearUsuarioExito() {
        Usuario usuarioInput = Usuario.builder()
                .dni("12345678")
                .nombres("Carlos")
                .apellidos("Santana")
                .rolCodigo("DOCENTE_INVESTIGADOR")
                .build();

        Rol rol = Rol.builder().codigoRol("DOCENTE_INVESTIGADOR").descripcion("Docente").build();

        when(rolRepository.findByCodigo("DOCENTE_INVESTIGADOR")).thenReturn(Optional.of(rol));
        when(usuarioRepository.existsByDni("12345678")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("carlos.santana@unas.edu.pe")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("encoded-password");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario result = crearUsuarioUseCase.execute(usuarioInput);

        assertNotNull(result);
        assertEquals("carlos.santana@unas.edu.pe", result.getCorreoInstitucional());
        assertEquals("encoded-password", result.getPasswordHash());
        assertTrue(result.isActivo());
        assertTrue(result.isMustChangePassword());

        verify(rolRepository).findByCodigo("DOCENTE_INVESTIGADOR");
        verify(usuarioRepository).existsByDni("12345678");
        verify(usuarioRepository).existsByCorreo("carlos.santana@unas.edu.pe");
        verify(passwordEncoder).encode("12345678");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when rol does not exist")
    void testCrearUsuarioRolNoExiste() {
        Usuario usuarioInput = Usuario.builder()
                .dni("12345678")
                .nombres("Carlos")
                .apellidos("Santana")
                .rolCodigo("ROL_INEXISTENTE")
                .build();

        when(rolRepository.findByCodigo("ROL_INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> crearUsuarioUseCase.execute(usuarioInput));

        verify(rolRepository).findByCodigo("ROL_INEXISTENTE");
        verifyNoInteractions(usuarioRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when DNI already exists")
    void testCrearUsuarioDniDuplicado() {
        Usuario usuarioInput = Usuario.builder()
                .dni("12345678")
                .nombres("Carlos")
                .apellidos("Santana")
                .rolCodigo("DOCENTE_INVESTIGADOR")
                .build();

        Rol rol = Rol.builder().codigoRol("DOCENTE_INVESTIGADOR").build();

        when(rolRepository.findByCodigo("DOCENTE_INVESTIGADOR")).thenReturn(Optional.of(rol));
        when(usuarioRepository.existsByDni("12345678")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> crearUsuarioUseCase.execute(usuarioInput));

        verify(rolRepository).findByCodigo("DOCENTE_INVESTIGADOR");
        verify(usuarioRepository).existsByDni("12345678");
        verifyNoMoreInteractions(usuarioRepository, passwordEncoder);
    }
}
