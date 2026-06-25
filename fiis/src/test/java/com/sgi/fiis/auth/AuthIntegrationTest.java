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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
        roleAdminEntity.setCode("ADMIN");
        roleAdminEntity.setDescription("Administrador");

        UserEntity adminEntity = new UserEntity();
        adminEntity.setId(1L);
        adminEntity.setDni("00000000");
        adminEntity.setFirstNames("Admin");
        adminEntity.setLastNames("Sistema");
        adminEntity.setInstitutionalEmail("admin@unas.edu.pe");
        adminEntity.setPasswordHash(passwordEncoder.encode("00000000"));
        adminEntity.setActive(true);
        adminEntity.setMustChangePassword(true);
        adminEntity.setRole(roleAdminEntity);

        // Stub Spring Data repository used by CustomUserDetailsService
        when(springDataUserRepository.findByInstitutionalEmail("admin@unas.edu.pe"))
                .thenReturn(Optional.of(adminEntity));

        // Stub domain repository used by UseCase or Controller
        User adminDomain = User.builder()
                .id(1L)
                .dni("00000000")
                .firstNames("Admin")
                .lastNames("Sistema")
                .institutionalEmail("admin@unas.edu.pe")
                .passwordHash(passwordEncoder.encode("00000000"))
                .active(true)
                .mustChangePassword(true)
                .roleCode("ADMIN")
                .roleDescription("Administrador")
                .build();
        when(userRepositoryPort.findByEmail("admin@unas.edu.pe")).thenReturn(Optional.of(adminDomain));

        // Make login request
        LoginRequestDto request = new LoginRequestDto("admin@unas.edu.pe", "00000000");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("admin@unas.edu.pe"))
                .andExpect(jsonPath("$.roleCode").value("ADMIN"))
                .andExpect(jsonPath("$.requiresVerification").value(false));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized for invalid credentials")
    void testLoginFailure() throws Exception {
        when(springDataUserRepository.findByInstitutionalEmail("admin@unas.edu.pe"))
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
                .firstNames("Jose")
                .lastNames("Evaristo")
                .institutionalEmail("jose.evaristo@unas.edu.pe")
                .phone("999888777")
                .password("securePassword123")
                .confirmPassword("securePassword123")
                .roleCode("DOCENTE")
                .build();

        // Stub exists check and save check
        when(userRepositoryPort.existsByDni("76543210")).thenReturn(false);
        when(userRepositoryPort.existsByEmail("jose.evaristo@unas.edu.pe")).thenReturn(false);

        User persistedUser = User.builder()
                .id(2L)
                .dni("76543210")
                .firstNames("Jose")
                .lastNames("Evaristo")
                .institutionalEmail("jose.evaristo@unas.edu.pe")
                .phone("999888777")
                .active(true)
                .mustChangePassword(false)
                .roleCode("DOCENTE")
                .build();
        when(userRepositoryPort.save(any())).thenReturn(persistedUser);
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
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value("jose.evaristo@unas.edu.pe"))
                .andExpect(jsonPath("$.roleCode").value("DOCENTE"));

        // Assert memory storage is cleaned
        assertNull(pendingRegistrationService.get("jose.evaristo@unas.edu.pe"));
    }

    @Test
    @DisplayName("Should successfully resend verification code when pending registration exists")
    void testResendCodeSuccess() throws Exception {
        RegisterRequestDto registerDto = RegisterRequestDto.builder()
                .dni("87654321")
                .firstNames("Maria")
                .lastNames("Del Carmen")
                .institutionalEmail("maria.carmen@unas.edu.pe")
                .phone("999111222")
                .password("securePassword123")
                .confirmPassword("securePassword123")
                .roleCode("DOCENTE")
                .build();

        when(userRepositoryPort.existsByDni("87654321")).thenReturn(false);
        when(userRepositoryPort.existsByEmail("maria.carmen@unas.edu.pe")).thenReturn(false);
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
        assertNotEquals(initialCode, updatedCode);

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

    @Test
    @DisplayName("Should return 401 JSON error when accessing protected endpoint without token")
    void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/users")
                        .header("Accept-Language", "es")
                        .locale(new java.util.Locale("es")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Acceso no autorizado. Debe iniciar sesión e incluir el token JWT en las cabeceras."))
                .andExpect(jsonPath("$.path").value("/api/v1/users"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}

