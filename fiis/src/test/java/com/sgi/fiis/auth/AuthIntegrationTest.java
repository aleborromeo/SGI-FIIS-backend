package com.sgi.fiis.auth;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.auth.application.dto.LoginRequestDto;
import com.sgi.fiis.users.domain.model.Rol;
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

    @MockitoBean
    private SpringDataUsuarioRepository springDataUsuarioRepository;

    @MockitoBean
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Test
    @DisplayName("Should login successfully with correct credentials and return JWT token")
    void testLoginSuccess() throws Exception {
        // Prepare entities
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
                .andExpect(jsonPath("$.mustChangePassword").value(true));
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
}
