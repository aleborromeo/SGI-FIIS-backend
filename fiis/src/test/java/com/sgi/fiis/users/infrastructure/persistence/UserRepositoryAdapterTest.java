package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryAdapter Unit Tests")
class UserRepositoryAdapterTest {

    @Mock
    private SpringDataUserRepository springDataRepository;

    @Mock
    private SpringDataRoleRepository roleRepository;

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(springDataRepository, roleRepository);
    }

    private RoleEntity createRoleEntity() {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setCode("DOCENTE");
        role.setDescription("Docente");
        return role;
    }

    private UserEntity createUserEntity(RoleEntity role) {
        UserEntity entity = new UserEntity();
        entity.setId(10L);
        entity.setDni("12345678");
        entity.setFirstNames("Juan");
        entity.setLastNames("Perez");
        entity.setInstitutionalEmail("juan.perez@unas.edu.pe");
        entity.setPhone("999888777");
        entity.setPasswordHash("hashedpassword");
        entity.setActive(true);
        entity.setMustChangePassword(false);
        entity.setRole(role);
        entity.setOauthProvider("azure");
        entity.setCreatedAt(LocalDateTime.of(2026, 6, 24, 12, 0));
        entity.setUpdatedAt(LocalDateTime.of(2026, 6, 24, 12, 0));
        return entity;
    }

    private User createUserModel() {
        return User.builder()
                .id(10L)
                .dni("12345678")
                .firstNames("Juan")
                .lastNames("Perez")
                .institutionalEmail("juan.perez@unas.edu.pe")
                .phone("999888777")
                .passwordHash("hashedpassword")
                .active(true)
                .mustChangePassword(false)
                .roleCode("DOCENTE")
                .roleDescription("Docente")
                .oauthProvider("azure")
                .createdAt(LocalDateTime.of(2026, 6, 24, 12, 0))
                .updatedAt(LocalDateTime.of(2026, 6, 24, 12, 0))
                .build();
    }

    @Test
    @DisplayName("Should successfully save user and map fields")
    void save_shouldSaveAndMapUser() {
        User userModel = createUserModel();
        RoleEntity roleEntity = createRoleEntity();
        UserEntity userEntity = createUserEntity(roleEntity);

        when(roleRepository.findByCode("DOCENTE")).thenReturn(Optional.of(roleEntity));
        when(springDataRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        User savedUser = adapter.save(userModel);

        assertNotNull(savedUser);
        assertEquals(10L, savedUser.getId());
        assertEquals("12345678", savedUser.getDni());
        assertEquals("Juan", savedUser.getFirstNames());
        assertEquals("Perez", savedUser.getLastNames());
        assertEquals("juan.perez@unas.edu.pe", savedUser.getInstitutionalEmail());
        assertEquals("DOCENTE", savedUser.getRoleCode());
        assertEquals("Docente", savedUser.getRoleDescription());
        assertEquals("azure", savedUser.getOauthProvider());
        assertTrue(savedUser.isActive());
        assertFalse(savedUser.isMustChangePassword());

        verify(roleRepository).findByCode("DOCENTE");
        verify(springDataRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when saving user with non-existent role")
    void save_roleNotFound_shouldThrowException() {
        User userModel = createUserModel();
        when(roleRepository.findByCode("DOCENTE")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> adapter.save(userModel));
        assertEquals("Role not found: DOCENTE", ex.getMessage());

        verify(roleRepository).findByCode("DOCENTE");
        verifyNoInteractions(springDataRepository);
    }

    @Test
    @DisplayName("Should find user by id")
    void findById_shouldReturnUser() {
        RoleEntity role = createRoleEntity();
        UserEntity entity = createUserEntity(role);
        when(springDataRepository.findById(10L)).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findById(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
        verify(springDataRepository).findById(10L);
    }

    @Test
    @DisplayName("Should return empty optional when user by id not found")
    void findById_notFound_shouldReturnEmpty() {
        when(springDataRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = adapter.findById(99L);

        assertFalse(result.isPresent());
        verify(springDataRepository).findById(99L);
    }

    @Test
    @DisplayName("Should find user by dni")
    void findByDni_shouldReturnUser() {
        RoleEntity role = createRoleEntity();
        UserEntity entity = createUserEntity(role);
        when(springDataRepository.findByDni("12345678")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByDni("12345678");

        assertTrue(result.isPresent());
        assertEquals("12345678", result.get().getDni());
        verify(springDataRepository).findByDni("12345678");
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_shouldReturnUser() {
        RoleEntity role = createRoleEntity();
        UserEntity entity = createUserEntity(role);
        when(springDataRepository.findByInstitutionalEmail("juan.perez@unas.edu.pe")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmail("juan.perez@unas.edu.pe");

        assertTrue(result.isPresent());
        assertEquals("juan.perez@unas.edu.pe", result.get().getInstitutionalEmail());
        verify(springDataRepository).findByInstitutionalEmail("juan.perez@unas.edu.pe");
    }

    @Test
    @DisplayName("Should find all users")
    void findAll_shouldReturnList() {
        RoleEntity role = createRoleEntity();
        UserEntity entity = createUserEntity(role);
        when(springDataRepository.findAll()).thenReturn(List.of(entity));

        List<User> result = adapter.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstNames());
        verify(springDataRepository).findAll();
    }

    @Test
    @DisplayName("Should search users by query")
    void search_shouldReturnMatchingUsers() {
        RoleEntity role = createRoleEntity();
        UserEntity entity = createUserEntity(role);
        when(springDataRepository.search("Juan")).thenReturn(List.of(entity));

        List<User> result = adapter.search("Juan");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getFirstNames());
        verify(springDataRepository).search("Juan");
    }

    @Test
    @DisplayName("Should check if user exists by DNI")
    void existsByDni_shouldReturnBoolean() {
        when(springDataRepository.existsByDni("12345678")).thenReturn(true);

        boolean exists = adapter.existsByDni("12345678");

        assertTrue(exists);
        verify(springDataRepository).existsByDni("12345678");
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void existsByEmail_shouldReturnBoolean() {
        when(springDataRepository.existsByInstitutionalEmail("juan.perez@unas.edu.pe")).thenReturn(false);

        boolean exists = adapter.existsByEmail("juan.perez@unas.edu.pe");

        assertFalse(exists);
        verify(springDataRepository).existsByInstitutionalEmail("juan.perez@unas.edu.pe");
    }
}
