package com.sgi.fiis.auth.application.usecase;

import com.sgi.fiis.auth.application.service.PendingResetPasswordService;
import com.sgi.fiis.auth.domain.port.PasswordEncoderPort;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: Restablecer contraseña usando un código de verificación.
 */
@Service
public class SelfResetPasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final PendingResetPasswordService pendingResetPasswordService;

    public SelfResetPasswordUseCase(UserRepositoryPort userRepository,
                                    PasswordEncoderPort passwordEncoder,
                                    PendingResetPasswordService pendingResetPasswordService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.pendingResetPasswordService = pendingResetPasswordService;
    }

    @Transactional
    public void execute(String email, String code, String newPassword, String confirmPassword) {
        // 1. Validar que las contraseñas coincidan
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("auth.password.mismatch");
        }

        String cleanEmail = email != null ? email.trim() : "";

        // 2. Validar el código de recuperación
        PendingResetPasswordService.ResetPasswordCode resetCode = pendingResetPasswordService.get(cleanEmail);
        if (resetCode == null) {
            throw new BusinessException("auth.code.invalid");
        }

        if (resetCode.isExpired()) {
            pendingResetPasswordService.remove(cleanEmail);
            throw new BusinessException("auth.code.expired");
        }

        if (!resetCode.getCode().equals(code)) {
            throw new BusinessException("auth.code.invalid");
        }

        // 3. Cargar el usuario
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new BusinessException("auth.user.not-found"));

        // 4. Encriptar y actualizar la contraseña
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.confirmPasswordChange(); // Limpia la bandera mustChangePassword ya que la cambia él mismo

        userRepository.save(user);

        // 5. Eliminar el código temporal
        pendingResetPasswordService.remove(cleanEmail);
    }
}
