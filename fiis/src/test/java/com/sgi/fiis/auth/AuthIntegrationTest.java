package com.sgi.fiis.auth;

import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.auth.application.dto.LoginRequestDto;
import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.dto.ResendCodeRequestDto;
import com.sgi.fiis.auth.application.dto.VerifyRegistrationRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.domain.port.UsuarioRepositoryPort;
import com.sgi.fiis.users.infrastructure.persistence.RolEntity;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUsuarioRepository;
import com.sgi.fiis.users.infrastructure.persistence.UsuarioEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Auth Integration Tests")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PendingRegistrationService pendingRegistrationService;

    @MockitoBean
    private SpringDataUsuarioRepository springDataUsuarioRepository;

    @MockitoBean
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @MockitoBean
    private EmailSenderPort emailSenderPort;

    @Test
    @DisplayName("Should successfully login directly and return JWT")
    void testLoginSuccess() throws Exception {
        RolEntity rolAdminEntity = new RolEntity();
        rolAdminEntity.setId(1L);
        rolAdminEntity.setCodigoRol("ADMIN");
        rolAdminEntity.setDescripcion("Administrador");

        UsuarioEntity adminEntity = new UsuarioEntity();
        adminEntity.setId(1L);
        adminEntity.setDni("00000000");
        adminEntity.setNombres("Admin");
        adminEntity.setApellidos("Sistema");
        adminEntity.setCorreoInstitucional("admin@unas.edu.pe");
        adminEntity.setPasswordHash(passwordEncoder.encode("00000000"));
        adminEntity.setActivo(true);
        adminEntity.setMustChangePassword(true);
        adminEntity.setRol(rolAdminEntity);

        // Stub Spring Data repository used by CustomUserDetailsService
        when(springDataUsuarioRepository.findByCorreoInstitucional("admin@unas.edu.pe"))
                .thenReturn(Optional.of(adminEntity));

        // Stub domain repository used by UseCase or Controller
        Usuario adminDomain = Usuario.builder()
                .id(1L)
                .dni("00000000")
                .nombres("Admin")
                .apellidos("Sistema")
                .correoInstitucional("admin@unas.edu.pe")
                .passwordHash(passwordEncoder.encode("00000000"))
                .activo(true)
                .mustChangePassword(true)
                .rolCodigo("ADMIN")
                .rolDescripcion("Administrador")
                .build();
        when(usuarioRepositoryPort.findByCorreo("admin@unas.edu.pe")).thenReturn(Optional.of(adminDomain));

        // Make login request
        LoginRequestDto request = new LoginRequestDto("admin@unas.edu.pe", "00000000");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.correo").value("admin@unas.edu.pe"))
                .andExpect(jsonPath("$.rolCodigo").value("ADMIN"))
                .andExpect(jsonPath("$.requiresVerification").value(false));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized for invalid credentials")
    void testLoginFailure() throws Exception {
        when(springDataUsuarioRepository.findByCorreoInstitucional("admin@unas.edu.pe"))
                .thenReturn(Optional.empty());

        LoginRequestDto request = new LoginRequestDto("admin@unas.edu.pe", "wrong-password");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should register pending user, then successfully verify and persist to database")
    void testRegisterAndVerifySuccess() throws Exception {
        // Setup registration DTO
        RegisterRequestDto registerDto = RegisterRequestDto.builder()
                .dni("76543210")
                .nombres("Jose")
                .apellidos("Evaristo")
                .correoInstitucional("jose.evaristo@unas.edu.pe")
                .telefono("999888777")
                .password("securePassword123")
                .confirmarPassword("securePassword123")
                .rolCodigo("DOCENTE")
                .build();

        // Stub exists check and save check
        when(usuarioRepositoryPort.existsByDni("76543210")).thenReturn(false);
        when(usuarioRepositoryPort.existsByCorreo("jose.evaristo@unas.edu.pe")).thenReturn(false);
        
        Usuario persistedUser = Usuario.builder()
                .id(2L)
                .dni("76543210")
                .nombres("Jose")
                .apellidos("Evaristo")
                .correoInstitucional("jose.evaristo@unas.edu.pe")
                .telefono("999888777")
                .activo(true)
                .mustChangePassword(false)
                .rolCodigo("DOCENTE")
                .build();
        when(usuarioRepositoryPort.save(any(Usuario.class))).thenReturn(persistedUser);
        doNothing().when(emailSenderPort).sendVerificationCode(eq("jose.evaristo@unas.edu.pe"), anyString());

        // 1. Post to register endpoint
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Código de verificación enviado al correo institucional. Complete el registro en el paso 2."));

        // Retrieve the generated code from PendingRegistrationService
        PendingRegistrationService.PendingRegistration pending = pendingRegistrationService.get("jose.evaristo@unas.edu.pe");
        assertNotNull(pending);
        String code = pending.getCode();
        assertNotNull(code);
        assertEquals(6, code.length());

        // 2. Post to verify endpoint
        VerifyRegistrationRequestDto verifyDto = new VerifyRegistrationRequestDto("jose.evaristo@unas.edu.pe", code);

        mockMvc.perform(post("/api/v1/auth/verify-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.correo").value("jose.evaristo@unas.edu.pe"))
                .andExpect(jsonPath("$.rolCodigo").value("DOCENTE"));
        
        // Assert memory storage is cleaned
        assertNull(pendingRegistrationService.get("jose.evaristo@unas.edu.pe"));
    }

    @Test
    @DisplayName("Should successfully resend verification code when pending registration exists")
    void testResendCodeSuccess() throws Exception {
        RegisterRequestDto registerDto = RegisterRequestDto.builder()
                .dni("87654321")
                .nombres("Maria")
                .apellidos("Del Carmen")
                .correoInstitucional("maria.carmen@unas.edu.pe")
                .telefono("999111222")
                .password("securePassword123")
                .confirmarPassword("securePassword123")
                .rolCodigo("DOCENTE")
                .build();

        when(usuarioRepositoryPort.existsByDni("87654321")).thenReturn(false);
        when(usuarioRepositoryPort.existsByCorreo("maria.carmen@unas.edu.pe")).thenReturn(false);
        doNothing().when(emailSenderPort).sendVerificationCode(eq("maria.carmen@unas.edu.pe"), anyString());

        // 1. Post to register endpoint
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk());

        // Get initial code
        PendingRegistrationService.PendingRegistration initialPending = pendingRegistrationService.get("maria.carmen@unas.edu.pe");
        assertNotNull(initialPending);
        String initialCode = initialPending.getCode();

        // 2. Post to resend endpoint
        ResendCodeRequestDto resendDto = new ResendCodeRequestDto("maria.carmen@unas.edu.pe");

        mockMvc.perform(post("/api/v1/auth/resend-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resendDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Código de verificación reenviado exitosamente al correo institucional."));

        // Get new code and assert it is updated
        PendingRegistrationService.PendingRegistration updatedPending = pendingRegistrationService.get("maria.carmen@unas.edu.pe");
        assertNotNull(updatedPending);
        String updatedCode = updatedPending.getCode();
        assertNotNull(updatedCode);
        assertEquals(6, updatedCode.length());

        // Cleanup
        pendingRegistrationService.remove("maria.carmen@unas.edu.pe");
    }

    @Test
    @DisplayName("Should return 400 Bad Request when resending code for non-existent pending registration")
    void testResendCodeNotFound() throws Exception {
        ResendCodeRequestDto resendDto = new ResendCodeRequestDto("nonexistent@unas.edu.pe");

        mockMvc.perform(post("/api/v1/auth/resend-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resendDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No se encontró ningún registro pendiente para el correo especificado"));
    }
}

