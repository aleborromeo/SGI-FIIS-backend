package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.Role;
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
@DisplayName("RoleRepositoryAdapter Unit Tests")
class RoleRepositoryAdapterTest {

    @Mock
    private SpringDataRoleRepository springDataRepository;

    @InjectMocks
    private RoleRepositoryAdapter adapter;

    @Test
    @DisplayName("Should find role by code")
    void testFindByCodigo() {
        RoleEntity entity = new RoleEntity();
        entity.setId(1L);
        entity.setRoleCode("ADMIN");
        entity.setDescription("Administrador");

        when(springDataRepository.findByRoleCode("ADMIN")).thenReturn(Optional.of(entity));

        Optional<Role> result = adapter.findByCode("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getRoleCode());
        assertEquals("Administrador", result.get().getDescription());
        verify(springDataRepository).findByRoleCode("ADMIN");
    }

    @Test
    @DisplayName("Should return empty optional when role by code is not found")
    void testFindByCodigoNotFound() {
        when(springDataRepository.findByRoleCode("GUEST")).thenReturn(Optional.empty());

        Optional<Role> result = adapter.findByCode("GUEST");

        assertFalse(result.isPresent());
        verify(springDataRepository).findByRoleCode("GUEST");
    }

    @Test
    @DisplayName("Should find all roles")
    void testFindAll() {
        RoleEntity entity = new RoleEntity();
        entity.setId(1L);
        entity.setRoleCode("ADMIN");
        entity.setDescription("Administrador");

        when(springDataRepository.findAll()).thenReturn(Collections.singletonList(entity));

        List<Role> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getRoleCode());
        verify(springDataRepository).findAll();
    }
}
