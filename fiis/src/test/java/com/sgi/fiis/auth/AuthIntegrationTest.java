package com.sgi.fiis.auth;

import com.sgi.fiis.auth.domain.port.EmailSenderPort;
import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.auth.application.dto.LoginRequestDto;
import com.sgi.fiis.auth.application.dto.RegisterRequestDto;
import com.sgi.fiis.auth.application.dto.ResendCodeRequestDto;
import com.sgi.fiis.auth.application.dto.VerifyRegistrationRequestDto;
import com.sgi.fiis.auth.application.service.PendingRegistrationService;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.users.domain.port.UserRepositoryPort;
import com.sgi.fiis.users.infrastructure.persistence.RoleEntity;
import com.sgi.fiis.users.infrastructure.persistence.SpringDataUserRepository;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
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
@SuppressWarnings("all")
class AuthIntegrationTest {

    private static final String ADMIN_EMAIL = "admin@unas.edu.pe";
    private static final String ADMIN_DNI = "00000000";
    private static final String JOSE_EMAIL = "jose.evaristo@unas.edu.pe";
    private static final String JOSE_DNI = "76543210";
    private static final String SECURE_PASSWORD = "securePassword123";
    private static final String MARIA_EMAIL = "maria.carmen@unas.edu.pe";
    private static final String ROLE_DOCENTE = "DOCENTE";
    private static final String ROLE_ADMIN = "ADMIN";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PendingRegistrationService pendingRegistrationService;

    @MockitoBean
    private SpringDataUserRepository springDataUserRepository;

    @MockitoBean
    private UserRepositoryPort userRepositoryPort;

    @MockitoBean
    private EmailSenderPort emailSenderPort;

    @Test
    @DisplayName("Should successfully login directly and return JWT")
    void testLoginSuccess() throws Exception {
        RoleEntity roleAdminEntity = new RoleEntity();
        roleAdminEntity.setId(1L);
        roleAdminEntity.setCode(ROLE_ADMIN);
        roleAdminEntity.setDescription("Administrador");

        UserEntity adminEntity = new UserEntity();
        adminEntity.setId(1L);
        adminEntity.setDni(ADMIN_DNI);
        adminEntity.setFirstNames("Admin");
        adminEntity.setLastNames("Sistema");
        adminEntity.setInstitutionalEmail(ADMIN_EMAIL);
        adminEntity.setPasswordHash(passwordEncoder.encode(ADMIN_DNI));
        adminEntity.setActive(true);
        adminEntity.setMustChangePassword(true);
        adminEntity.setRole(roleAdminEntity);

        // Stub Spring Data repository used by CustomUserDetailsService
        when(springDataUserRepository.findByInstitutionalEmail(ADMIN_EMAIL))
                .thenReturn(Optional.of(adminEntity));

        // Stub domain repository used by UseCase or Controller
        User adminDomain = User.builder()
                .id(1L)
                .dni(ADMIN_DNI)
                .firstNames("Admin")
                .lastNames("Sistema")
                .institutionalEmail(ADMIN_EMAIL)
                .passwordHash(passwordEncoder.encode(ADMIN_DNI))
                .active(true)
                .mustChangePassword(true)
                .roleCode(ROLE_ADMIN)
                .roleDescription("Administrador")
                .build();
        when(userRepositoryPort.findByEmail(ADMIN_EMAIL)).thenReturn(Optional.of(adminDomain));

        // Make login request
        LoginRequestDto request = new LoginRequestDto(ADMIN_EMAIL, ADMIN_DNI);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value(ADMIN_EMAIL))
                .andExpect(jsonPath("$.roleCode").value(ROLE_ADMIN))
                .andExpect(jsonPath("$.requiresVerification").value(false));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized for invalid credentials")
    void testLoginFailure() throws Exception {
        when(springDataUserRepository.findByInstitutionalEmail(ADMIN_EMAIL))
                .thenReturn(Optional.empty());

        LoginRequestDto request = new LoginRequestDto(ADMIN_EMAIL, "wrong-password");

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
                .dni(JOSE_DNI)
                .firstNames("Jose")
                .lastNames("Evaristo")
                .institutionalEmail(JOSE_EMAIL)
                .phone("999888777")
                .password(SECURE_PASSWORD)
                .confirmPassword(SECURE_PASSWORD)
                .roleCode(ROLE_DOCENTE)
                .build();

        // Stub exists check and save check
        when(userRepositoryPort.existsByDni(JOSE_DNI)).thenReturn(false);
        when(userRepositoryPort.existsByEmail(JOSE_EMAIL)).thenReturn(false);

        User persistedUser = User.builder()
                .id(2L)
                .dni(JOSE_DNI)
                .firstNames("Jose")
                .lastNames("Evaristo")
                .institutionalEmail(JOSE_EMAIL)
                .phone("999888777")
                .active(true)
                .mustChangePassword(false)
                .roleCode(ROLE_DOCENTE)
                .build();
        when(userRepositoryPort.save(any(User.class))).thenReturn(persistedUser);
        doNothing().when(emailSenderPort).sendVerificationCode(eq(JOSE_EMAIL), anyString());

        // 1. Post to register endpoint
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registro exitoso. Verifique su correo para activar la cuenta."));

        // Retrieve the generated code from PendingRegistrationService
        PendingRegistrationService.PendingRegistration pending = pendingRegistrationService.get(JOSE_EMAIL);
        assertNotNull(pending);
        String code = pending.getCode();
        assertNotNull(code);
        assertEquals(6, code.length());

        // 2. Post to verify endpoint
        VerifyRegistrationRequestDto verifyDto = new VerifyRegistrationRequestDto(JOSE_EMAIL, code);

        mockMvc.perform(post("/api/v1/auth/verify-registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value(JOSE_EMAIL))
                .andExpect(jsonPath("$.roleCode").value(ROLE_DOCENTE));

        // Assert memory storage is cleaned
        assertNull(pendingRegistrationService.get(JOSE_EMAIL));
    }

    @Test
    @DisplayName("Should successfully resend verification code when pending registration exists")
    void testResendCodeSuccess() throws Exception {
        RegisterRequestDto registerDto = RegisterRequestDto.builder()
                .dni("87654321")
                .firstNames("Maria")
                .lastNames("Del Carmen")
                .institutionalEmail(MARIA_EMAIL)
                .phone("999111222")
                .password(SECURE_PASSWORD)
                .confirmPassword(SECURE_PASSWORD)
                .roleCode(ROLE_DOCENTE)
                .build();

        when(userRepositoryPort.existsByDni("87654321")).thenReturn(false);
        when(userRepositoryPort.existsByEmail(MARIA_EMAIL)).thenReturn(false);
        doNothing().when(emailSenderPort).sendVerificationCode(eq(MARIA_EMAIL), anyString());

        // 1. Post to register endpoint
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk());

        // Get initial code
        PendingRegistrationService.PendingRegistration initialPending = pendingRegistrationService.get(MARIA_EMAIL);
        assertNotNull(initialPending);
        String initialCode = initialPending.getCode();

        // 2. Post to resend endpoint
        ResendCodeRequestDto resendDto = new ResendCodeRequestDto(MARIA_EMAIL);

        mockMvc.perform(post("/api/v1/auth/resend-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resendDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("C\u00F3digo de verificación reenviado exitosamente."));

        // Get new code and assert it is updated
        PendingRegistrationService.PendingRegistration updatedPending = pendingRegistrationService.get(MARIA_EMAIL);
        assertNotNull(updatedPending);
        String updatedCode = updatedPending.getCode();
        assertNotNull(updatedCode);
        assertEquals(6, updatedCode.length());
        assertNotEquals(initialCode, updatedCode);

        // Cleanup
        pendingRegistrationService.remove(MARIA_EMAIL);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when resending code for non-existent pending registration")
    void testResendCodeNotFound() throws Exception {
        ResendCodeRequestDto resendDto = new ResendCodeRequestDto("nonexistent@unas.edu.pe");

        mockMvc.perform(post("/api/v1/auth/resend-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resendDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("auth.register.pending-not-found"));
    }
}
