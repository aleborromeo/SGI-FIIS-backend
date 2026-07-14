package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * Use case: Login (RF-01, RF-02).
 * Validates credentials, checks active user and generates JWT.
 */
@Service
public class LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginUseCase(UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponseDto execute(String email, String password) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("auth.credentials.invalid"));

        // RF-02: Validate that the user is active
        if (!user.isActive()) {
            throw new BusinessException("auth.user.inactive");
        }

        // Validate password
        if (user.getPasswordHash() == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("auth.credentials.invalid");
        }

        // Generate JWT
        String token = tokenProvider.generateToken(user.getInstitutionalEmail(), user.getRoleCode());

        return LoginResponseDto.builder()
                .id(user.getId())
                .token(token)
                .type("Bearer")
                .email(user.getInstitutionalEmail())
                .firstNames(user.getFirstNames())
                .lastNames(user.getLastNames())
                .roleCode(user.getRoleCode())
                .mustChangePassword(user.isMustChangePassword())
                .build();
    }
}
