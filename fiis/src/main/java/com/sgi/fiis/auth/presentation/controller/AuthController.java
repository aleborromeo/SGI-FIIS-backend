package com.sgi.fiis.auth.presentation.controller;

import com.sgi.fiis.auth.application.dto.ChangePasswordDto;
import com.sgi.fiis.auth.application.dto.ForgotPasswordRequestDto;
import com.sgi.fiis.auth.application.dto.ResetPasswordRequestDto;
import com.sgi.fiis.auth.application.dto.LoginRequestDto;
import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.dto.ResendCodeRequestDto;
import com.sgi.fiis.auth.application.dto.VerifyRegistrationRequestDto;
import com.sgi.fiis.auth.application.usecase.ChangePasswordUseCase;
import com.sgi.fiis.auth.application.usecase.ForgotPasswordUseCase;
import com.sgi.fiis.auth.application.usecase.SelfResetPasswordUseCase;
import com.sgi.fiis.auth.application.usecase.LoginUseCase;
import com.sgi.fiis.auth.application.usecase.RegisterUseCase;
import com.sgi.fiis.auth.application.usecase.ResendCodeUseCase;
import com.sgi.fiis.auth.application.usecase.VerifyRegistrationUseCase;
import com.sgi.fiis.users.application.dto.UserResponseDto;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import com.sgi.fiis.users.presentation.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final String MESSAGE_KEY = "message";

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final VerifyRegistrationUseCase verifyRegistrationUseCase;
    private final ResendCodeUseCase resendCodeUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final SelfResetPasswordUseCase selfResetPasswordUseCase;
    private final UserRepositoryPort userRepository;
    private final UserMapper userMapper;
    private final MessageSource messageSource;

    private static final String KEY_MESSAGE = "message";

    @SuppressWarnings("java:S107")
    public AuthController(LoginUseCase loginUseCase,
            RegisterUseCase registerUseCase,
            VerifyRegistrationUseCase verifyRegistrationUseCase,
            ResendCodeUseCase resendCodeUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            ForgotPasswordUseCase forgotPasswordUseCase,
            SelfResetPasswordUseCase selfResetPasswordUseCase,
            UserRepositoryPort userRepository,
            UserMapper userMapper,
            MessageSource messageSource) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.verifyRegistrationUseCase = verifyRegistrationUseCase;
        this.resendCodeUseCase = resendCodeUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.selfResetPasswordUseCase = selfResetPasswordUseCase;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.messageSource = messageSource;
    }

    /** RF-01, RF-02: Iniciar sesión directo */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        LoginResponseDto response = loginUseCase.execute(dto.getEmail(), dto.getPassword());
        return ResponseEntity.ok(response);
    }

    /** Auto-registro: Paso 1 (envía código) */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDto dto) {
        registerUseCase.execute(dto);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("auth.register.success", null, locale);
        return ResponseEntity.ok(Map.of(KEY_MESSAGE, message));
    }

    /** Auto-registro: Reenviar código */
    @PostMapping("/resend-code")
    public ResponseEntity<Map<String, String>> resendCode(@Valid @RequestBody ResendCodeRequestDto dto) {
        resendCodeUseCase.execute(dto);
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("auth.resend-code.success", null, locale);
        return ResponseEntity.ok(Map.of(KEY_MESSAGE, message));
    }

    /** Auto-registro: Paso 2 (verifica código y guarda usuario) */
    @PostMapping("/verify-registration")
    public ResponseEntity<LoginResponseDto> verifyRegistration(@Valid @RequestBody VerifyRegistrationRequestDto dto) {
        LoginResponseDto response = verifyRegistrationUseCase.execute(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(response);
    }

    /** Solicitar recuperación de contraseña (envía código) */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto dto) {
        forgotPasswordUseCase.execute(dto.getEmail());
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("auth.forgot-password.success", null, "Código de recuperación enviado con éxito.", locale);
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, message));
    }

    /** Restablecer contraseña usando código */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto dto) {
        selfResetPasswordUseCase.execute(dto.getEmail(), dto.getCode(), dto.getNewPassword(), dto.getConfirmPassword());
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("auth.reset-password.success", null, "Contraseña restablecida con éxito.", locale);
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, message));
    }

    /** RF-06: Cambiar contraseña */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordDto dto) {
        String email = authentication.getName();
        changePasswordUseCase.execute(email, dto.getCurrentPassword(), dto.getNewPassword());
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("auth.change-password.success", null, locale);
        return ResponseEntity.ok(Map.of(KEY_MESSAGE, message));
    }

    /** RF-03: Obtener perfil del usuario autenticado */
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }
}
