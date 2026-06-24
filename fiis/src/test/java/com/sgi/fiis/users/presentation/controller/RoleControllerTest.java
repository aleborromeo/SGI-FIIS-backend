package com.sgi.fiis.users.presentation.controller;

import com.sgi.fiis.users.domain.model.Role;
import com.sgi.fiis.users.domain.port.RoleRepositoryPort;
import com.sgi.fiis.users.presentation.mapper.RoleMapper;
import com.sgi.fiis.users.application.dto.RoleResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("RoleController Integration Tests")
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleRepositoryPort roleRepository;

    @MockitoBean
    private RoleMapper roleMapper;

    @Test
    @DisplayName("Should successfully list roles when authenticated")
    void testListRolesSuccess() throws Exception {
        Role role1 = Role.builder().id(1L).code("ADMIN").description("Administrador").build();
        Role role2 = Role.builder().id(2L).code("ESTUDIANTE").description("Estudiante").build();

        RoleResponseDto dto1 = RoleResponseDto.builder().id(1L).code("ADMIN").description("Administrador").build();
        RoleResponseDto dto2 = RoleResponseDto.builder().id(2L).code("ESTUDIANTE").description("Estudiante").build();

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));
        when(roleMapper.toResponseDto(role1)).thenReturn(dto1);
        when(roleMapper.toResponseDto(role2)).thenReturn(dto2);

        mockMvc.perform(get("/api/v1/roles")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("admin@unas.edu.pe").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("ADMIN"))
                .andExpect(jsonPath("$[1].code").value("ESTUDIANTE"));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when not authenticated")
    void testListRolesUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/roles"))
                .andExpect(status().isUnauthorized());
    }
}
