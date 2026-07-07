package com.sgi.fiis.observations.presentation.controller;

import tools.jackson.databind.ObjectMapper;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ObservationController Integration Tests")
class ObservationControllerTest {

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
