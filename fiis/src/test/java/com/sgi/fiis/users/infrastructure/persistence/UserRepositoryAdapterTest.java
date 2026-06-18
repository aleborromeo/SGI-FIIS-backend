package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.User;
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
@DisplayName("UserRepositoryAdapter Unit Tests")
class UserRepositoryAdapterTest {

    @Mock
    private SpringDataUserRepository springDataRepository;

    @Mock
    private SpringDataRoleRepository roleRepository;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private RoleEntity getTestRoleEntity() {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setRoleCode("ADMIN");
        role.setDescription("Administrador");
        return role;
    }

    private UserEntity getTestUserEntity() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setDni("12345678");
        user.setFirstName("Juan");
        user.setLastName("Perez");
        user.setInstitutionalEmail("juan.perez@unas.edu.pe");
        user.setRole(getTestRoleEntity());
        user.setActive(true);
        user.setMustChangePassword(true);
        return user;
    }

    private User getTestUser() {
        return User.builder()
                .id(1L)
                .dni("12345678")
                .firstName("Juan")
                .lastName("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .roleCode("ADMIN")
                .active(true)
                .mustChangePassword(true)
                .build();
    }

    @Test
    @DisplayName("Should save a user successfully")
    void testSave() {
        User domain = getTestUser();
        UserEntity entity = getTestUserEntity();

        when(roleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(getTestRoleEntity()));
        when(springDataRepository.save(any(UserEntity.class))).thenReturn(entity);

        User result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getDni(), result.getDni());
        verify(roleRepository).findByRoleCode("ADMIN");
        verify(springDataRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw Exception when saving user with non-existent role")
    void testSaveRoleNotFound() {
        User domain = getTestUser();
        when(roleRepository.findByRoleCode("ADMIN")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> adapter.save(domain));
        verify(roleRepository).findByRoleCode("ADMIN");
        verifyNoInteractions(springDataRepository);
    }

    @Test
    @DisplayName("Should find user by ID")
    void testFindById() {
        UserEntity entity = getTestUserEntity();
        when(springDataRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("12345678", result.get().getDni());
        verify(springDataRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find user by DNI")
    void testFindByDni() {
        UserEntity entity = getTestUserEntity();
        when(springDataRepository.findByDni("12345678")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByDni("12345678");

        assertTrue(result.isPresent());
        assertEquals("12345678", result.get().getDni());
        verify(springDataRepository).findByDni("12345678");
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByCorreo() {
        UserEntity entity = getTestUserEntity();
        when(springDataRepository.findByInstitutionalEmail("juan.perez@unas.edu.pe")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmail("juan.perez@unas.edu.pe");

        assertTrue(result.isPresent());
        assertEquals("juan.perez@unas.edu.pe", result.get().getInstitutionalEmail());
        verify(springDataRepository).findByInstitutionalEmail("juan.perez@unas.edu.pe");
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        UserEntity entity = getTestUserEntity();
        when(springDataRepository.findAll()).thenReturn(Collections.singletonList(entity));

        List<User> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals("12345678", result.get(0).getDni());
        verify(springDataRepository).findAll();
    }

    @Test
    @DisplayName("Should search users by query")
    void testSearch() {
        UserEntity entity = getTestUserEntity();
        when(springDataRepository.search("Juan")).thenReturn(Collections.singletonList(entity));

        List<User> result = adapter.search("Juan");

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
    @DisplayName("Should check if user exists by email")
    void testExistsByCorreo() {
        when(springDataRepository.existsByInstitutionalEmail("juan.perez@unas.edu.pe")).thenReturn(true);

        assertTrue(adapter.existsByEmail("juan.perez@unas.edu.pe"));
        verify(springDataRepository).existsByInstitutionalEmail("juan.perez@unas.edu.pe");
    }
}
