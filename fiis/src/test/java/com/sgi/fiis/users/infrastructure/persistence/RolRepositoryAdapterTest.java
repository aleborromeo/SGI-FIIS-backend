package com.sgi.fiis.users.infrastructure.persistence;

import com.sgi.fiis.users.domain.model.Rol;
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
@DisplayName("RolRepositoryAdapter Unit Tests")
class RolRepositoryAdapterTest {

    @Mock
    private SpringDataRolRepository springDataRepository;

    @InjectMocks
    private RolRepositoryAdapter adapter;

    @Test
    @DisplayName("Should find rol by code")
    void testFindByCodigo() {
        RolEntity entity = new RolEntity();
        entity.setId(1L);
        entity.setCodigoRol("ADMIN");
        entity.setDescripcion("Administrador");

        when(springDataRepository.findByCodigoRol("ADMIN")).thenReturn(Optional.of(entity));

        Optional<Rol> result = adapter.findByCodigo("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getCodigoRol());
        assertEquals("Administrador", result.get().getDescripcion());
        verify(springDataRepository).findByCodigoRol("ADMIN");
    }

    @Test
    @DisplayName("Should return empty optional when rol by code is not found")
    void testFindByCodigoNotFound() {
        when(springDataRepository.findByCodigoRol("GUEST")).thenReturn(Optional.empty());

        Optional<Rol> result = adapter.findByCodigo("GUEST");

        assertFalse(result.isPresent());
        verify(springDataRepository).findByCodigoRol("GUEST");
    }

    @Test
    @DisplayName("Should find all roles")
    void testFindAll() {
        RolEntity entity = new RolEntity();
        entity.setId(1L);
        entity.setCodigoRol("ADMIN");
        entity.setDescripcion("Administrador");

        when(springDataRepository.findAll()).thenReturn(Collections.singletonList(entity));

        List<Rol> result = adapter.findAll();

        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getCodigoRol());
        verify(springDataRepository).findAll();
    }
}
