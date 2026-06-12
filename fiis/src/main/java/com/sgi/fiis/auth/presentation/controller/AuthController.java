package com.sgi.fiis.auth.presentation.controller;

import com.sgi.fiis.auth.application.dto.CambiarPasswordDto;
import com.sgi.fiis.auth.application.dto.LoginRequestDto;
import com.sgi.fiis.auth.application.dto.LoginResponseDto;
import com.sgi.fiis.auth.application.usecase.CambiarPasswordUseCase;
import com.sgi.fiis.auth.application.usecase.LoginUseCase;
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
    private final CambiarPasswordUseCase cambiarPasswordUseCase;
    private final UsuarioRepositoryPort usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public AuthController(LoginUseCase loginUseCase,
                          CambiarPasswordUseCase cambiarPasswordUseCase,
                          UsuarioRepositoryPort usuarioRepository,
                          UsuarioMapper usuarioMapper) {
        this.loginUseCase = loginUseCase;
        this.cambiarPasswordUseCase = cambiarPasswordUseCase;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    /** RF-01, RF-02: Iniciar sesión */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        LoginResponseDto response = loginUseCase.execute(dto.getCorreo(), dto.getPassword());
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

    /** Error de autenticación OAuth2 (Microsoft) */
    @GetMapping("/oauth2/error")
    public ResponseEntity<Map<String, String>> oauthError() {
        return ResponseEntity.status(401)
                .body(Map.of("error", "Error al autenticar con Microsoft"));
    }
}
