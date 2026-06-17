package com.sgi.fiis.auth.infrastructure.security;

import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
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
@DisplayName("OAuthUserHandlerAdapter Unit Tests")
class OAuthUserHandlerAdapterTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordEncoderPort passwordEncoder;

    @InjectMocks
    private OAuthUserHandlerAdapter adapter;

    @Test
    @DisplayName("Should return existing user from OAuth")
    void testFindOrCreateFromOAuthExisting() {
        Usuario existingUser = Usuario.builder()
                .id(1L)
                .correoInstitucional("existing@unas.edu.pe")
                .rolCodigo("ADMIN")
                .build();

        when(usuarioRepository.findByCorreo("existing@unas.edu.pe")).thenReturn(Optional.of(existingUser));

        Usuario result = adapter.findOrCreateFromOAuth("existing@unas.edu.pe", "Existing User", "microsoft");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("existing@unas.edu.pe", result.getCorreoInstitucional());
        verify(usuarioRepository).findByCorreo("existing@unas.edu.pe");
        verifyNoMoreInteractions(usuarioRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    @DisplayName("Should create new user from OAuth if not exists")
    void testFindOrCreateFromOAuthCreateNew() {
        when(usuarioRepository.findByCorreo("new@unas.edu.pe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-placeholder-password");
        
        Usuario savedUser = Usuario.builder()
                .id(100L)
                .dni("OA123456")
                .nombres("New")
                .apellidos("User")
                .correoInstitucional("new@unas.edu.pe")
                .rolCodigo("ESTUDIANTE")
                .activo(true)
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        Usuario result = adapter.findOrCreateFromOAuth("new@unas.edu.pe", "New User", "microsoft");

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("new@unas.edu.pe", result.getCorreoInstitucional());
        verify(usuarioRepository).findByCorreo("new@unas.edu.pe");
        verify(passwordEncoder).encode(anyString());
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
