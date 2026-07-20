package com.sgi.fiis.auth.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomUserDetails Unit Tests")
class CustomUserDetailsTest {

    @Test
    @DisplayName("Should create CustomUserDetails and retrieve correct values")
    void testCustomUserDetailsCreation() {
        CustomUserDetails userDetails = new CustomUserDetails(
                42L,
                "user@example.com",
                "password",
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "USER"
        );

        assertEquals(42L, userDetails.getId());
        assertEquals("user@example.com", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void testEqualsAndHashCode() {
        CustomUserDetails user1 = new CustomUserDetails(
                1L,
                "user@example.com",
                "password",
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "USER"
        );

        CustomUserDetails user2 = new CustomUserDetails(
                1L,
                "user@example.com",
                "password",
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "USER"
        );

        CustomUserDetails user3 = new CustomUserDetails(
                2L,
                "user@example.com",
                "password",
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                "USER"
        );

        // Same reference
        assertEquals(user1, user1);
        
        // Equal instances
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());

        // Different IDs
        assertNotEquals(user1, user3);
        assertNotEquals(null, user1);
        assertNotEquals("some-string", user1);
    }
}
