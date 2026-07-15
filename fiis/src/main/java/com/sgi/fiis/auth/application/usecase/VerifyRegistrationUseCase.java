package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.auth.domain.port.TokenProviderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.User;
import java.time.ZoneId;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Use case: Verify self-registration (Step 2).
 * Validates the temporary verification code. If correct, persists the user in
 * DB and generates the final JWT.
 */
@Service
public class VerifyRegistrationUseCase {

    private final PendingRegistrationService pendingRegistrationService;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public VerifyRegistrationUseCase(PendingRegistrationService pendingRegistrationService,
            UserRepositoryPort userRepository,
            PasswordEncoderPort passwordEncoder,
            TokenProviderPort tokenProvider) {
        this.pendingRegistrationService = pendingRegistrationService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResponseDto execute(String email, String code) {
        PendingRegistrationService.PendingRegistration pending = pendingRegistrationService.get(email);

        // 1. Validate that a pending registration exists
        if (pending == null) {
            throw new BusinessException("auth.register.pending-not-found");
        }

        // 2. Validate that the code has not expired
        if (pending.isExpired()) {
            pendingRegistrationService.remove(email);
            throw new BusinessException("auth.register.expired");
        }

        // 3. Validate the verification code
        if (!pending.getCode().equals(code)) {
            throw new BusinessException("auth.register.invalid-code");
        }

        // 4. Persist the user in the database
        RegisterRequestDto dto = pending.getRequestDto();
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        User newUser = User.builder()
                .dni(dto.getDni())
                .firstNames(dto.getFirstNames())
                .lastNames(dto.getLastNames())
                .institutionalEmail(dto.getInstitutionalEmail().trim())
                .phone(dto.getPhone())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .active(true) // Activated upon verifying email
                .mustChangePassword(false) // Self-registration doesn't require initial password change
                .roleCode(dto.getRoleCode())
                .createdAt(now)
                .updatedAt(now)
                .build();

        User saved = userRepository.save(newUser);

        // 5. Clean up the pending registration in memory
        pendingRegistrationService.remove(email);

        // 6. Generate JWT to log in immediately
        String token = tokenProvider.generateToken(saved.getInstitutionalEmail(), saved.getRoleCode());

        return LoginResponseDto.builder()
                .id(saved.getId())
                .token(token)
                .type("Bearer")
                .email(saved.getInstitutionalEmail())
                .firstNames(saved.getFirstNames())
                .lastNames(saved.getLastNames())
                .roleCode(saved.getRoleCode())
                .mustChangePassword(saved.isMustChangePassword())
                .requiresVerification(false)
                .build();
    }
}
