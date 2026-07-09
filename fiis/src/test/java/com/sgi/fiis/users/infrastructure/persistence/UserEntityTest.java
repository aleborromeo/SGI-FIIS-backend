package com.sgi.fiis.users.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserEntity Unit Tests")
class UserEntityTest {

    @Test
    @DisplayName("Should set and get fields correctly")
    void testGettersAndSetters() {
        UserEntity user = new UserEntity();
        RoleEntity role = new RoleEntity();
        role.setId(1L);
        role.setCode("ADMIN");
        role.setDescription("Administrador");

        LocalDateTime now = LocalDateTime.now();

        user.setId(1L);
        user.setDni("12345678");
        user.setFirstNames("Juan");
        user.setLastNames("Perez");
        user.setInstitutionalEmail("juan.perez@unas.edu.pe");
        user.setPhone("987654321");
        user.setPasswordHash("hashed_pwd");
        user.setActive(true);
        user.setMustChangePassword(false);
        user.setRole(role);
        user.setOauthProvider("MICROSOFT");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("12345678", user.getDni());
        assertEquals("Juan", user.getFirstNames());
        assertEquals("Perez", user.getLastNames());
        assertEquals("juan.perez@unas.edu.pe", user.getInstitutionalEmail());
        assertEquals("987654321", user.getPhone());
        assertEquals("hashed_pwd", user.getPasswordHash());
        assertTrue(user.isActive());
        assertFalse(user.isMustChangePassword());
        assertEquals(role, user.getRole());
        assertEquals("MICROSOFT", user.getOauthProvider());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }
}
