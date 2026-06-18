package com.sgi.fiis.observaciones.presentation.controller;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.observaciones.application.dto.ObservacionRequestDTO;
import com.sgi.fiis.observaciones.application.dto.ObservacionResponseDTO;
import com.sgi.fiis.observaciones.application.dto.SubsanacionRequestDTO;
import com.sgi.fiis.observaciones.application.dto.SubsanacionResponseDTO;
import com.sgi.fiis.observaciones.application.usecase.*;
import com.sgi.fiis.observaciones.domain.exception.ObservacionNotFoundException;
import com.sgi.fiis.observaciones.domain.exception.SubsanacionInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ObservacionController Integration Tests")
class ObservacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegistraObservacionUseCase registraObservacionUseCase;

    @MockitoBean
    private RegistraSubsanacionUseCase registraSubsanacionUseCase;

    @MockitoBean
    private ListaObservacionesPorTramiteUseCase listaObservacionesPorTramiteUseCase;

    @MockitoBean
    private ConsultaObservacionUseCase consultaObservacionUseCase;

    @MockitoBean
    private ListaSubsanacionesPorObservacionUseCase listaSubsanacionesPorObservacionUseCase;

    @Test
    @DisplayName("Should successfully register an observation")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    void testRegistrarObservacionSuccess() throws Exception {
        ObservacionRequestDTO request = ObservacionRequestDTO.builder()
                .idTramite(1)
                .idRevisor(10)
                .tipoObservacion("TECNICA")
                .descripcion("Falta firma")
                .rolRevisor("COORDINADOR_GRUPO")
                .build();

        ObservacionResponseDTO response = ObservacionResponseDTO.builder()
                .id(100)
                .idTramite(1)
                .idRevisor(10)
                .tipoObservacion("TECNICA")
                .descripcion("Falta firma")
                .estado("PENDIENTE")
                .rolRevisor("COORDINADOR_GRUPO")
                .build();

        when(registraObservacionUseCase.execute(any(ObservacionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/observaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.descripcion").value("Falta firma"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Should successfully register a subsanacion")
    @WithMockUser(username = "student@unas.edu.pe", roles = {"ESTUDIANTE"})
    void testRegistrarSubsanacionSuccess() throws Exception {
        SubsanacionRequestDTO request = SubsanacionRequestDTO.builder()
                .idSolicitante(5)
                .descripcion("He subido el documento")
                .idDocumentoAdjunto(45)
                .build();

        SubsanacionResponseDTO response = SubsanacionResponseDTO.builder()
                .id(200)
                .idObservacion(100)
                .idSolicitante(5)
                .descripcion("He subido el documento")
                .idDocumentoAdjunto(45)
                .build();

        when(registraSubsanacionUseCase.execute(any(Integer.class), any(SubsanacionRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/observaciones/100/subsanar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.idObservacion").value(100))
                .andExpect(jsonPath("$.descripcion").value("He subido el documento"));
    }

    @Test
    @DisplayName("Should list observations for a tramite")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    void testListarPorTramiteSuccess() throws Exception {
        ObservacionResponseDTO response = ObservacionResponseDTO.builder()
                .id(100)
                .idTramite(1)
                .tipoObservacion("TECNICA")
                .descripcion("Falta firma")
                .estado("PENDIENTE")
                .build();

        when(listaObservacionesPorTramiteUseCase.execute(1)).thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/observaciones/tramite/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].descripcion").value("Falta firma"));
    }

    @Test
    @DisplayName("Should get observation by id")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    void testConsultarObservacionSuccess() throws Exception {
        ObservacionResponseDTO response = ObservacionResponseDTO.builder()
                .id(100)
                .idTramite(1)
                .tipoObservacion("TECNICA")
                .descripcion("Falta firma")
                .estado("PENDIENTE")
                .build();

        when(consultaObservacionUseCase.execute(100)).thenReturn(response);

        mockMvc.perform(get("/api/observaciones/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.descripcion").value("Falta firma"));
    }

    @Test
    @DisplayName("Should return 404 when observation not found")
    @WithMockUser(username = "admin@unas.edu.pe", roles = {"ADMIN"})
    void testConsultarObservacionNotFound() throws Exception {
        when(consultaObservacionUseCase.execute(999)).thenThrow(new ObservacionNotFoundException(999));

        mockMvc.perform(get("/api/observaciones/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Observación no encontrada con ID: 999"));
    }

    @Test
    @DisplayName("Should return 409 Conflict when subsanacion is invalid")
    @WithMockUser(username = "student@unas.edu.pe", roles = {"ESTUDIANTE"})
    void testRegistrarSubsanacionConflict() throws Exception {
        SubsanacionRequestDTO request = SubsanacionRequestDTO.builder()
                .idSolicitante(5)
                .descripcion("Test")
                .build();

        when(registraSubsanacionUseCase.execute(any(Integer.class), any(SubsanacionRequestDTO.class)))
                .thenThrow(new SubsanacionInvalidaException(100));

        mockMvc.perform(post("/api/observaciones/100/subsanar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("No se puede subsanar la observación con ID: 100. Solo se pueden subsanar observaciones en estado PENDIENTE."));
    }
}
