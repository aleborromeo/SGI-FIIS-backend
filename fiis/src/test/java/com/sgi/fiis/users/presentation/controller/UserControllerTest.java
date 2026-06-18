package com.sgi.fiis.users.presentation.controller;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.users.application.dto.UserRequestDto;
import com.sgi.fiis.users.application.usecase.*;
import com.sgi.fiis.users.domain.model.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private ListarUsuariosUseCase listarUsuariosUseCase; // Wait, ListarUsuariosUseCase or ListUsersUseCase? Ah! In UserController we used ListUsersUseCase!
    // Let's make sure we mock ListUsersUseCase instead of ListarUsuariosUseCase.
    @MockitoBean
    private ListUsersUseCase listUsersUseCase;

    @MockitoBean
    private ObtenerUsuarioUseCase obtenerUsuarioUseCase; // Wait, ObtenerUsuarioUseCase or GetUserUseCase? In UserController we used GetUserUseCase!
    @MockitoBean
    private GetUserUseCase getUserUseCase;

    @Test
    @DisplayName("Should successfully create a user when authenticated as ADMIN")
    void testCrearUsuarioSuccess() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .dni("12345678")
                .firstName("Maria")
                .lastName("Gomez")
                .institutionalEmail("maria.gomez@unas.edu.pe")
                .phone("999888777")
                .roleCode("ESTUDIANTE")
                .build();

        User userDomain = User.builder()
                .id(2L)
                .dni("12345678")
                .firstName("Maria")
                .lastName("Gomez")
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
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.roleCode").value("ESTUDIANTE"));
    }

    @Test
    @DisplayName("Should return 403 Forbidden when trying to create user as ESTUDIANTE")
    void testCrearUsuarioForbidden() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .dni("12345678")
                .firstName("Maria")
                .lastName("Gomez")
                .institutionalEmail("maria.gomez@unas.edu.pe")
                .phone("999888777")
                .roleCode("ESTUDIANTE")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("student@unas.edu.pe").roles("ESTUDIANTE"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when not authenticated")
    void testCrearUsuarioUnauthorized() throws Exception {
        UserRequestDto request = UserRequestDto.builder()
                .dni("12345678")
                .firstName("Maria")
                .lastName("Gomez")
                .institutionalEmail("maria.gomez@unas.edu.pe")
                .phone("999888777")
                .roleCode("ESTUDIANTE")
                .build();

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should successfully list users when authenticated as ADMIN")
    void testListarUsuariosSuccess() throws Exception {
        User userDomain = User.builder()
                .id(1L)
                .dni("00000000")
                .firstName("Admin")
                .lastName("Sistema")
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
}
