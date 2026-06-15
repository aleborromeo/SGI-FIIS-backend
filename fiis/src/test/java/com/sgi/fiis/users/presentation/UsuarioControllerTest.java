package com.sgi.fiis.users.presentation.controller;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.users.application.dto.UsuarioRequestDto;
import com.sgi.fiis.users.application.usecase.*;
import com.sgi.fiis.users.domain.model.Usuario;
import com.sgi.fiis.users.presentation.mapper.UsuarioMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
@DisplayName("UsuarioController Integration Tests")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CrearUsuarioUseCase crearUsuarioUseCase;

    @MockitoBean
    private ListarUsuariosUseCase listarUsuariosUseCase;

    @MockitoBean
    private ObtenerUsuarioUseCase obtenerUsuarioUseCase;

    @Test
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    @DisplayName("Should successfully create a user when authenticated as ADMIN")
    void testCrearUsuarioSuccess() throws Exception {
        UsuarioRequestDto request = UsuarioRequestDto.builder()
                .dni("12345678")
                .nombres("Maria")
                .apellidos("Gomez")
                .correoInstitucional("maria.gomez@unas.edu.pe")
                .telefono("999888777")
                .rolCodigo("ESTUDIANTE")
                .build();

        Usuario userDomain = Usuario.builder()
                .id(2L)
                .dni("12345678")
                .nombres("Maria")
                .apellidos("Gomez")
                .correoInstitucional("maria.gomez@unas.edu.pe")
                .telefono("999888777")
                .activo(true)
                .mustChangePassword(true)
                .rolCodigo("ESTUDIANTE")
                .build();

        when(crearUsuarioUseCase.execute(any(Usuario.class))).thenReturn(userDomain);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.dni").value("12345678"))
                .andExpect(jsonPath("$.nombres").value("Maria"))
                .andExpect(jsonPath("$.rolCodigo").value("ESTUDIANTE"));
    }

    @Test
    @WithMockUser(username = "student@unas.edu.pe", roles = {"ESTUDIANTE"})
    @DisplayName("Should return 403 Forbidden when trying to create user as ESTUDIANTE")
    void testCrearUsuarioForbidden() throws Exception {
        UsuarioRequestDto request = UsuarioRequestDto.builder()
                .dni("12345678")
                .nombres("Maria")
                .apellidos("Gomez")
                .correoInstitucional("maria.gomez@unas.edu.pe")
                .telefono("999888777")
                .rolCodigo("ESTUDIANTE")
                .build();

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when not authenticated")
    void testCrearUsuarioUnauthorized() throws Exception {
        UsuarioRequestDto request = UsuarioRequestDto.builder()
                .dni("12345678")
                .nombres("Maria")
                .apellidos("Gomez")
                .correoInstitucional("maria.gomez@unas.edu.pe")
                .telefono("999888777")
                .rolCodigo("ESTUDIANTE")
                .build();

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    @DisplayName("Should successfully list users when authenticated as ADMIN")
    void testListarUsuariosSuccess() throws Exception {
        Usuario userDomain = Usuario.builder()
                .id(1L)
                .dni("00000000")
                .nombres("Admin")
                .apellidos("Sistema")
                .correoInstitucional("admin@unas.edu.pe")
                .activo(true)
                .rolCodigo("ADMIN")
                .build();

        when(listarUsuariosUseCase.execute(null)).thenReturn(Collections.singletonList(userDomain));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correoInstitucional").value("admin@unas.edu.pe"))
                .andExpect(jsonPath("$[0].rolCodigo").value("ADMIN"));
    }
}
