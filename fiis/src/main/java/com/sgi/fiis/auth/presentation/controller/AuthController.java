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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
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
    private final JdbcTemplate jdbcTemplate;

    private static final String KEY_MESSAGE = "message";

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

    /** Public stats for WelcomePage (no auth required) */
    @GetMapping("/public-stats")
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        int proyectosRegistrados = count("SELECT COUNT(*) FROM proyectos");
        int tesis = count("SELECT COUNT(*) FROM planes_tesis");
        int docentesInvestigadores = count("SELECT COUNT(*) FROM usuarios WHERE es_activo = TRUE");
        int gruposInvestigacion = count("SELECT COUNT(*) FROM grupos_investigacion WHERE es_activo = TRUE");
        int proyectosCulminados = count("SELECT COUNT(*) FROM proyectos WHERE estado_proyecto = 'FINALIZADO'");

        return ResponseEntity.ok(Map.of(
                "proyectosRegistrados", proyectosRegistrados,
                "tesis", tesis,
                "docentesInvestigadores", docentesInvestigadores,
                "gruposInvestigacion", gruposInvestigacion,
                "proyectosCulminados", proyectosCulminados
        ));
    }

    /** Public groups for WelcomePage (no auth required) */
    @GetMapping("/public-groups")
    public ResponseEntity<List<Map<String, Object>>> getPublicGroups() {
        String sql = """
                SELECT g.codigo_grupo AS codigo,
                       g.nombre_grupo AS nombre,
                       COUNT(DISTINCT m.id_usuario) AS miembros,
                       COUNT(DISTINCT p.id_proyecto) AS publicaciones
                FROM grupos_investigacion g
                LEFT JOIN membresias_grupo m ON m.id_grupo = g.id_grupo AND m.es_activo = TRUE
                LEFT JOIN proyectos p ON p.id_grupo = g.id_grupo
                WHERE g.es_activo = TRUE
                GROUP BY g.codigo_grupo, g.nombre_grupo
                ORDER BY publicaciones DESC
                """;
        List<Map<String, Object>> groups = jdbcTemplate.queryForList(sql);
        return ResponseEntity.ok(groups);
    }

    private int count(String sql) {
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return result != null ? result : 0;
    }
}
