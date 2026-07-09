package com.sgi.fiis.users.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoleEntity Unit Tests")
class RoleEntityTest {

    @Test
    @DisplayName("Should set and get fields correctly")
    void testGettersAndSetters() {
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setCode("ADMIN");
        role.setDescription("Administrador del sistema");

        assertEquals(1L, role.getId());
        assertEquals("ADMIN", role.getCode());
        assertEquals("Administrador del sistema", role.getDescription());
    }
}
