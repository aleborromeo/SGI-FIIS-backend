package com.sgi.fiis.auth.presentation.controller;

import com.sgi.fiis.auth.application.dto.CambiarPasswordDto;
import com.sgi.fiis.auth.application.dto.LoginRequestDto;
import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.dto.VerifyRegistrationRequestDto;
import com.sgi.fiis.auth.application.usecase.CambiarPasswordUseCase;
import com.sgi.fiis.auth.application.usecase.LoginUseCase;
import com.sgi.fiis.auth.application.usecase.RegisterUseCase;
import com.sgi.fiis.auth.application.usecase.VerifyRegistrationUseCase;
import com.sgi.fiis.users.application.dto.UsuarioResponseDto;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import com.sgi.fiis.users.presentation.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final VerifyRegistrationUseCase verifyRegistrationUseCase;
    private final CambiarPasswordUseCase cambiarPasswordUseCase;
    private final UsuarioRepositoryPort usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public AuthController(LoginUseCase loginUseCase,
                          RegisterUseCase registerUseCase,
                          VerifyRegistrationUseCase verifyRegistrationUseCase,
                          CambiarPasswordUseCase cambiarPasswordUseCase,
                          UsuarioRepositoryPort usuarioRepository,
                          UsuarioMapper usuarioMapper) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.verifyRegistrationUseCase = verifyRegistrationUseCase;
        this.cambiarPasswordUseCase = cambiarPasswordUseCase;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    /** RF-01, RF-02: Iniciar sesión directo */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        LoginResponseDto response = loginUseCase.execute(dto.getCorreo(), dto.getPassword());
        return ResponseEntity.ok(response);
    }

    /** Auto-registro: Paso 1 (envía código) */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDto dto) {
        registerUseCase.execute(dto);
        return ResponseEntity.ok(Map.of("message", "Código de verificación enviado al correo institucional. Complete el registro en el paso 2."));
    }

    /** Auto-registro: Paso 2 (verifica código y guarda usuario) */
    @PostMapping("/verify-registration")
    public ResponseEntity<LoginResponseDto> verifyRegistration(@Valid @RequestBody VerifyRegistrationRequestDto dto) {
        LoginResponseDto response = verifyRegistrationUseCase.execute(dto.getCorreo(), dto.getCodigo());
        return ResponseEntity.ok(response);
    }

    /** RF-06: Cambiar contraseña */
    @PostMapping("/cambiar-password")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            Authentication authentication,
            @Valid @RequestBody CambiarPasswordDto dto) {
        String correo = authentication.getName();
        cambiarPasswordUseCase.execute(correo, dto.getPasswordActual(), dto.getPasswordNueva());
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
    }

    /** RF-03: Obtener perfil del usuario autenticado */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDto> getProfile(Authentication authentication) {
        String correo = authentication.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(usuarioMapper.toResponseDto(usuario));
    }
}
