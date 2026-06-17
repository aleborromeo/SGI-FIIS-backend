package com.sgi.fiis.auth.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtTokenProvider Unit Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    // Minimum 32 characters key for HMAC-SHA256
    private final String secret = "my-secret-key-32-chars-long-or-more-for-jwt";
    private final long expirationMs = 60000; // 1 minute

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    @DisplayName("Should generate token successfully")
    void testGenerateToken() {
        String token = tokenProvider.generateToken("user@example.com", "ADMIN");
        assertNotNull(token);
        assertFalse(token.trim().isEmpty());
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateTokenSuccess() {
        String token = tokenProvider.generateToken("user@example.com", "ADMIN");
        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should return false when validating invalid token")
    void testValidateTokenInvalid() {
        assertFalse(tokenProvider.validateToken("invalid-token"));
    }

    @Test
    @DisplayName("Should extract email successfully from token")
    void testGetEmailFromToken() {
        String email = "test@example.com";
        String token = tokenProvider.generateToken(email, "USER");
        String extractedEmail = tokenProvider.getEmailFromToken(token);
        assertEquals(email, extractedEmail);
    }
}
