package com.sgi.fiis.observations.presentation.controller;

import com.sgi.fiis.TestcontainersConfig;
import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.observations.application.dto.ObservationRequestDTO;
import com.sgi.fiis.observations.application.dto.ObservationResponseDTO;
import com.sgi.fiis.observations.application.dto.RemedyRequestDTO;
import com.sgi.fiis.observations.application.dto.RemedyResponseDTO;
import com.sgi.fiis.observations.application.usecase.*;
import com.sgi.fiis.observations.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observations.domain.exception.InvalidRemedyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ObservationController Integration Tests")
class ObservationControllerTest extends TestcontainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterObservationUseCase registerObservationUseCase;

    @MockitoBean
    private RegisterRemedyUseCase registerRemedyUseCase;

    @MockitoBean
    private ListObservationsByProcedureUseCase listObservationsByProcedureUseCase;

    @MockitoBean
    private GetObservationUseCase getObservationUseCase;

    @MockitoBean
    private ListRemediesByObservationUseCase listRemediesByObservationUseCase;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Should successfully list remedies for an observation")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    void testListRemediesSuccess() throws Exception {
        RemedyResponseDTO response = RemedyResponseDTO.builder()
                .id(200)
                .observationId(100)
                .description("He subido el documento")
                .build();

        when(listRemediesByObservationUseCase.execute(100)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/observations/100/remedies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(200))
                .andExpect(jsonPath("$[0].description").value("He subido el documento"));
    }

    @Test
    @DisplayName("Should list observations for the current user's procedures")
    void testListMyObservationsSuccess() throws Exception {
        CustomUserDetails userDetails = new CustomUserDetails(5L, "student@unas.edu.pe", "pass", true, Collections.emptyList(), "ESTUDIANTE");

        org.springframework.jdbc.core.RowMapper<ObservationResponseDTO> mapper = any();
        when(jdbcTemplate.query(anyString(), mapper, eq(5L)))
                .thenReturn(List.of(ObservationResponseDTO.builder().id(300).description("Falta anexos").build()));

        mockMvc.perform(get("/api/observations/my")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(300))
                .andExpect(jsonPath("$[0].description").value("Falta anexos"));
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException and return 400 Bad Request")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"COORDINADOR_GRUPO"})
    void testRegisterObservationIllegalArgument() throws Exception {
        when(registerObservationUseCase.execute(any())).thenThrow(new IllegalArgumentException("Argumento inválido"));

        mockMvc.perform(post("/api/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Argumento inválido"));
    }

    @Test
    @DisplayName("Should successfully register an observation")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"COORDINADOR_GRUPO"})
    void testRegistrarObservacionSuccess() throws Exception {
        ObservationRequestDTO request = ObservationRequestDTO.builder()
                .procedureId(1)
                .reviewerId(10)
                .type("TECNICA")
                .description("Falta firma")
                .reviewerRole("COORDINADOR_GRUPO")
                .build();

        ObservationResponseDTO response = ObservationResponseDTO.builder()
                .id(100)
                .procedureId(1)
                .reviewerId(10)
                .type("TECNICA")
                .description("Falta firma")
                .status("PENDIENTE")
                .reviewerRole("COORDINADOR_GRUPO")
                .build();

        when(registerObservationUseCase.execute(any(ObservationRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/observations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.description").value("Falta firma"))
                .andExpect(jsonPath("$.status").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Should successfully register a remedy")
    @WithMockUser(username = "student@unas.edu.pe", roles = {"ESTUDIANTE"})
    void testRegistrarRemedySuccess() throws Exception {
        RemedyRequestDTO request = RemedyRequestDTO.builder()
                .applicantId(5)
                .description("He subido el documento")
                .attachedDocumentId(45)
                .build();

        RemedyResponseDTO response = RemedyResponseDTO.builder()
                .id(200)
                .observationId(100)
                .applicantId(5)
                .description("He subido el documento")
                .attachedDocumentId(45)
                .build();

        when(registerRemedyUseCase.execute(any(Integer.class), any(RemedyRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/observations/100/remedy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.observationId").value(100))
                .andExpect(jsonPath("$.description").value("He subido el documento"));
    }

    @Test
    @DisplayName("Should list observations for a procedure")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    void testListarPorTramiteSuccess() throws Exception {
        ObservationResponseDTO response = ObservationResponseDTO.builder()
                .id(100)
                .procedureId(1)
                .type("TECNICA")
                .description("Falta firma")
                .status("PENDIENTE")
                .build();

        when(listObservationsByProcedureUseCase.execute(1)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/observations/procedure/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].description").value("Falta firma"));
    }

    @Test
    @DisplayName("Should get observation by id")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    void testConsultarObservacionSuccess() throws Exception {
        ObservationResponseDTO response = ObservationResponseDTO.builder()
                .id(100)
                .procedureId(1)
                .type("TECNICA")
                .description("Falta firma")
                .status("PENDIENTE")
                .build();

        when(getObservationUseCase.execute(100)).thenReturn(response);

        mockMvc.perform(get("/api/observations/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.description").value("Falta firma"));
    }

    @Test
    @DisplayName("Should return 404 when observation not found")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    void testConsultarObservacionNotFound() throws Exception {
        when(getObservationUseCase.execute(999)).thenThrow(new ObservationNotFoundException(999));

        mockMvc.perform(get("/api/observations/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Observación no encontrada con ID: 999"));
    }

    @Test
    @DisplayName("Should return 409 Conflict when remedy is invalid")
    @WithMockUser(username = "student@unas.edu.pe", roles = {"ESTUDIANTE"})
    void testRegistrarRemedyConflict() throws Exception {
        RemedyRequestDTO request = RemedyRequestDTO.builder()
                .applicantId(5)
                .description("Test")
                .build();

        when(registerRemedyUseCase.execute(any(Integer.class), any(RemedyRequestDTO.class)))
                .thenThrow(new InvalidRemedyException(100));

        mockMvc.perform(post("/api/observations/100/remedy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("No se puede subsanar la observación con ID: 100. Solo se pueden subsanar observaciones en estado PENDIENTE."));
    }
}
