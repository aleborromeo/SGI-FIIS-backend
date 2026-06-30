package com.sgi.fiis.auth.infrastructure.security;

import com.sgi.fiis.auth.domain.port.OAuthUserHandlerPort;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Adapter that implements OAuthUserHandlerPort.
 * Finds an existing user by institutional email;
 * if it doesn't exist, creates a new one with minimum details from the OAuth provider.
 */
@Component
public class OAuthUserHandlerAdapter implements OAuthUserHandlerPort {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public OAuthUserHandlerAdapter(UserRepositoryPort userRepository,
                                   PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findOrCreateFromOAuth(String email, String name, String provider) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> createOAuthUser(email, name, provider));
    }

    /**
     * Creates a new user from OAuth details.
     * - DNI: placeholder "OA" + short UUID (maximum 8 characters)
     * - Password: random hashed UUID (will not be used for login)
     * - Role: ESTUDIANTE by default
     * - active: true
     * - mustChangePassword: false (does not apply to OAuth)
     */
    private User createOAuthUser(String email, String name, String provider) {
        // Generate placeholder DNI (8 chars max by DB constraint)
        String dniPlaceholder = "OA" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 6).toUpperCase();

        // Password placeholder hashed (user won't use it)
        String passwordPlaceholder = passwordEncoder.encode(UUID.randomUUID().toString());

        // Split first and last name
        String[] parts = name.trim().split("\\s+", 2);
        String firstNames = parts[0];
        String lastNames = parts.length > 1 ? parts[1] : "";

        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        User newUser = User.builder()
                .dni(dniPlaceholder)
                .firstNames(firstNames)
                .lastNames(lastNames)
                .institutionalEmail(email)
                .passwordHash(passwordPlaceholder)
                .active(true)
                .mustChangePassword(false)
                .roleCode("ESTUDIANTE")
                .oauthProvider(provider)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return userRepository.save(newUser);
    }
}
