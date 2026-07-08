package com.sgi.fiis.users.presentation.controller;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.users.application.dto.UserRequestDto;
import com.sgi.fiis.users.application.dto.UserUpdateDto;
import com.sgi.fiis.users.application.usecase.*;
import com.sgi.fiis.users.domain.model.User;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController Integration Tests")
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private CreateUserUseCase createUserUseCase;

        @MockitoBean
        private ListUsersUseCase listUsersUseCase;

        @MockitoBean
        private GetUserUseCase getUserUseCase;

        @MockitoBean
        private UpdateUserUseCase updateUserUseCase;

        @MockitoBean
        private ToggleUserStatusUseCase toggleUserStatusUseCase;

        @MockitoBean
        private ResetPasswordUseCase resetPasswordUseCase;

    @Test
    @DisplayName("Should successfully create a user when authenticated as ADMIN")
    void testCreateUserSuccess() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .dni("12345678")
                .firstNames("Maria")
                .lastNames("Gomez")
                .institutionalEmail("maria.gomez@unas.edu.pe")
                .phone("999888777")
                .roleCode("ESTUDIANTE")
                .build();

        User userDomain = User.builder()
                .id(2L)
                .dni("12345678")
                .firstNames("Maria")
                .lastNames("Gomez")
                .institutionalEmail("maria.gomez@unas.edu.pe")
                .phone("999888777")
                .active(true)
                .mustChangePassword(true)
                .roleCode("ESTUDIANTE")
                .build();

        when(createUserUseCase.execute(any(User.class))).thenReturn(userDomain);

        mockMvc.perform(post("/api/v1/users")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin@unas.edu.pe").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.dni").value("12345678"))
                .andExpect(jsonPath("$.firstNames").value("Maria"))
                .andExpect(jsonPath("$.roleCode").value("ESTUDIANTE"));
    }

    @Test
    @DisplayName("Should return 403 Forbidden when trying to create user as ESTUDIANTE")
    void testCreateUserForbidden() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .dni("12345678")
                .firstNames("Maria")
                .lastNames("Gomez")
                .institutionalEmail("maria.gomez@unas.edu.pe")
                .phone("999888777")
                .roleCode("ESTUDIANTE")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("student@unas.edu.pe").roles("ESTUDIANTE"))
                        .header("Accept-Language", "es")
                        .locale(new java.util.Locale("es"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Acceso denegado. No tiene los privilegios necesarios para acceder a este recurso."))
                .andExpect(jsonPath("$.path").value("/api/v1/users"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

        @Test
        @DisplayName("Should return 401 Unauthorized when not authenticated")
        void testCreateUserUnauthorized() throws Exception {
                UserRequestDto request = UserRequestDto.builder()
                                .dni("12345678")
                                .firstNames("Maria")
                                .lastNames("Gomez")
                                .institutionalEmail("maria.gomez@unas.edu.pe")
                                .phone("999888777")
                                .roleCode("ESTUDIANTE")
                                .build();

                mockMvc.perform(post("/api/v1/users")
                                .header("Accept-Language", "es")
                                .locale(new java.util.Locale("es"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.status").value(401))
                                .andExpect(jsonPath("$.error").value("Unauthorized"))
                                .andExpect(jsonPath("$.message").value("Acceso no autorizado. Debe iniciar sesión e incluir el token JWT en las cabeceras."))
                                .andExpect(jsonPath("$.path").value("/api/v1/users"))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

    @Test
    @DisplayName("Should successfully list users when authenticated as ADMIN")
    void testListUsersSuccess() throws Exception {
        User userDomain = User.builder()
                .id(1L)
                .dni("00000000")
                .firstNames("Admin")
                .lastNames("Sistema")
                .institutionalEmail("admin@unas.edu.pe")
                .active(true)
                .roleCode("ADMIN")
                .build();

        when(listUsersUseCase.execute(null)).thenReturn(Collections.singletonList(userDomain));

        mockMvc.perform(get("/api/v1/users")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin@unas.edu.pe").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].institutionalEmail").value("admin@unas.edu.pe"))
                .andExpect(jsonPath("$[0].roleCode").value("ADMIN"));
    }

    @Test
    @DisplayName("Should successfully get a user by ID when authenticated as ADMIN")
    void testGetUserSuccess() throws Exception {
        User userDomain = User.builder()
                .id(1L)
                .dni("00000000")
                .firstNames("Admin")
                .lastNames("Sistema")
                .institutionalEmail("admin@unas.edu.pe")
                .active(true)
                .roleCode("ADMIN")
                .build();

        when(getUserUseCase.execute(1L)).thenReturn(userDomain);

        mockMvc.perform(get("/api/v1/users/{id}", 1L)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin@unas.edu.pe").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstNames").value("Admin"))
                .andExpect(jsonPath("$.roleCode").value("ADMIN"));
    }

    @Test
    @DisplayName("Should successfully update a user when authenticated as ADMIN")
    void testUpdateUserSuccess() throws Exception {
        UserUpdateDto request = UserUpdateDto.builder()
                .firstNames("Maria Modificada")
                .lastNames("Gomez")
                .institutionalEmail("maria.gomez@unas.edu.pe")
                .phone("999888777")
                .roleCode("ESTUDIANTE")
                .build();

        User userDomain = User.builder()
                .id(2L)
                .dni("12345678")
                .firstNames("Maria Modificada")
                .lastNames("Gomez")
                .institutionalEmail("maria.gomez@unas.edu.pe")
                .phone("999888777")
                .active(true)
                .roleCode("ESTUDIANTE")
                .build();

        when(updateUserUseCase.execute(2L, "Maria Modificada", "Gomez", "maria.gomez@unas.edu.pe", "999888777", "ESTUDIANTE"))
                .thenReturn(userDomain);

        mockMvc.perform(put("/api/v1/users/{id}", 2L)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin@unas.edu.pe").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.firstNames").value("Maria Modificada"));
    }

    @Test
    @DisplayName("Should successfully toggle status when authenticated as ADMIN")
    void testToggleStatusSuccess() throws Exception {
        User userDomain = User.builder()
                .id(2L)
                .dni("12345678")
                .firstNames("Maria")
                .lastNames("Gomez")
                .active(false)
                .roleCode("ESTUDIANTE")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(
                1L, "admin@unas.edu.pe", "password", true,
                Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        when(toggleUserStatusUseCase.execute(2L, false, 1L)).thenReturn(userDomain);

        mockMvc.perform(patch("/api/v1/users/{id}/status", 2L)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonMap("active", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @DisplayName("Should successfully reset password when authenticated as ADMIN")
    void testResetPasswordSuccess() throws Exception {
        mockMvc.perform(patch("/api/v1/users/{id}/reset-password", 2L)
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin@unas.edu.pe").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Contraseña reiniciada exitosamente"));

        org.mockito.Mockito.verify(resetPasswordUseCase).execute(2L);
    }
}
