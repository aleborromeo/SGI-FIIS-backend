package com.sgi.fiis.auth.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BcryptPasswordEncoderAdapter Unit Tests")
class BcryptPasswordEncoderAdapterTest {

    private final BcryptPasswordEncoderAdapter adapter = new BcryptPasswordEncoderAdapter();

    @Test
    @DisplayName("Should encode password successfully")
    void testEncode() {
        String rawPassword = "mySecretPassword";
        String encoded = adapter.encode(rawPassword);

        assertNotNull(encoded);
        assertNotEquals(rawPassword, encoded);
        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$") || encoded.startsWith("$2y$"));
    }

    @Test
    @DisplayName("Should return true when password matches")
    void testMatchesSuccess() {
        String rawPassword = "mySecretPassword";
        String encoded = adapter.encode(rawPassword);

        assertTrue(adapter.matches(rawPassword, encoded));
    }

    @Test
    @DisplayName("Should return false when password does not match")
    void testMatchesFailure() {
        String rawPassword = "mySecretPassword";
        String wrongPassword = "wrongPassword";
        String encoded = adapter.encode(rawPassword);

        assertFalse(adapter.matches(wrongPassword, encoded));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when encoded password is null")
    void testMatchesNullEncodedPassword() {
        assertThrows(IllegalArgumentException.class, () -> adapter.matches("mySecretPassword", null));
    }
}
