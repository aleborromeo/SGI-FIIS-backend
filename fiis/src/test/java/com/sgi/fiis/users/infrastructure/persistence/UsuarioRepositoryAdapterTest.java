package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioRepositoryAdapter Unit Tests")
class UsuarioRepositoryAdapterTest {

    @Mock
    private SpringDataUsuarioRepository springDataRepository;

    @Mock
    private SpringDataRolRepository rolRepository;

    @InjectMocks
    private UsuarioRepositoryAdapter adapter;

    private RolEntity getTestRolEntity() {
        RolEntity rol = new RolEntity();
        rol.setId(1L);
        rol.setCodigoRol("ADMIN");
        rol.setDescripcion("Administrador");
        return rol;
    }

    private UsuarioEntity getTestUsuarioEntity() {
        UsuarioEntity user = new UsuarioEntity();
        user.setId(1L);
        user.setDni("12345678");
        user.setNombres("Juan");
        user.setApellidos("Perez");
        user.setCorreoInstitucional("juan.perez@unas.edu.pe");
        user.setRol(getTestRolEntity());
        user.setActivo(true);
        user.setMustChangePassword(true);
        return user;
    }

    private Usuario getTestUsuario() {
        return Usuario.builder()
                .id(1L)
                .dni("12345678")
                .nombres("Juan")
                .apellidos("Perez")
                .correoInstitucional("juan.perez@unas.edu.pe")
                .rolCodigo("ADMIN")
                .activo(true)
                .mustChangePassword(true)
                .build();
    }

    @Test
    @DisplayName("Should save a user successfully")
    void testSave() {
        Usuario domain = getTestUsuario();
        UsuarioEntity entity = getTestUsuarioEntity();

        when(rolRepository.findByCodigoRol("ADMIN")).thenReturn(Optional.of(getTestRolEntity()));
        when(springDataRepository.save(any(UsuarioEntity.class))).thenReturn(entity);

        Usuario result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getDni(), result.getDni());
        verify(rolRepository).findByCodigoRol("ADMIN");
        verify(springDataRepository).save(any(UsuarioEntity.class));
    }

    @Test
    @DisplayName("Should throw Exception when saving user with non-existent role")
    void testSaveRoleNotFound() {
        Usuario domain = getTestUsuario();
        when(rolRepository.findByCodigoRol("ADMIN")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adapter.save(domain));
        verify(rolRepository).findByCodigoRol("ADMIN");
        verifyNoInteractions(springDataRepository);
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindById() {
        UsuarioEntity entity = getTestUsuarioEntity();
        when(springDataRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Usuario> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("12345678", result.get().getDni());
        verify(springDataRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find user by DNI")
    void testFindByDni() {
        UsuarioEntity entity = getTestUsuarioEntity();
        when(springDataRepository.findByDni("12345678")).thenReturn(Optional.of(entity));

        Optional<Usuario> result = adapter.findByDni("12345678");

        assertTrue(result.isPresent());
        assertEquals("12345678", result.get().getDni());
        verify(springDataRepository).findByDni("12345678");
    }

    @Test
    @DisplayName("Should find user by correo")
    void testFindByCorreo() {
        UsuarioEntity entity = getTestUsuarioEntity();
        when(springDataRepository.findByCorreoInstitucional("juan.perez@unas.edu.pe")).thenReturn(Optional.of(entity));

        Optional<Usuario> result = adapter.findByCorreo("juan.perez@unas.edu.pe");

        assertTrue(result.isPresent());
        assertEquals("juan.perez@unas.edu.pe", result.get().getCorreoInstitucional());
        verify(springDataRepository).findByCorreoInstitucional("juan.perez@unas.edu.pe");
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        UsuarioEntity entity = getTestUsuarioEntity();
        when(springDataRepository.findAll()).thenReturn(Collections.singletonList(entity));

        List<Usuario> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals("12345678", result.get(0).getDni());
        verify(springDataRepository).findAll();
    }

    @Test
    @DisplayName("Should search users by query")
    void testSearch() {
        UsuarioEntity entity = getTestUsuarioEntity();
        when(springDataRepository.search("Juan")).thenReturn(Collections.singletonList(entity));

        List<Usuario> result = adapter.search("Juan");

        assertEquals(1, result.size());
        assertEquals("12345678", result.get(0).getDni());
        verify(springDataRepository).search("Juan");
    }

    @Test
    @DisplayName("Should check if user exists by DNI")
    void testExistsByDni() {
        when(springDataRepository.existsByDni("12345678")).thenReturn(true);

        assertTrue(adapter.existsByDni("12345678"));
        verify(springDataRepository).existsByDni("12345678");
    }

    @Test
    @DisplayName("Should check if user exists by correo")
    void testExistsByCorreo() {
        when(springDataRepository.existsByCorreoInstitucional("juan.perez@unas.edu.pe")).thenReturn(true);

        assertTrue(adapter.existsByCorreo("juan.perez@unas.edu.pe"));
        verify(springDataRepository).existsByCorreoInstitucional("juan.perez@unas.edu.pe");
    }
}
