package com.sgi.fiis.thesis.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.*;
import com.sgi.fiis.thesis.domain.exception.ThesisReportNotFoundException;
import com.sgi.fiis.thesis.domain.exception.InvalidStateTransitionException;
import com.sgi.fiis.thesis.domain.port.in.ThesisReportUseCase;
import com.sgi.fiis.thesis.presentation.handler.ThesisExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ThesisReportControllerTest {

    private MockMvc mockMvc;
    private ThesisReportUseCase thesisReportUseCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        thesisReportUseCase = mock(ThesisReportUseCase.class);
        var controller = new ThesisReportController(thesisReportUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ThesisExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private ThesisReportResponse buildResponse(Integer id) {
        return new ThesisReportResponse(
                id, 1, "Informe Final", 200, null, ThesisReportStatus.EN_REVISION
        );
    }

    @Test
    void registrar_shouldReturn201() throws Exception {
        var command = new RegisterThesisReportCommand(1, "Informe Final", 200);
        var response = buildResponse(1);

        when(thesisReportUseCase.registrarInformeFinal(any(RegisterThesisReportCommand.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/thesis/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/thesis/reports/1"))
                .andExpect(jsonPath("$.idInformeTesis").value(1));
    }

    @Test
    void registrar_shouldReturn400_whenInvalidBody() throws Exception {
        var command = new RegisterThesisReportCommand(null, "", null);

        mockMvc.perform(post("/api/v1/thesis/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtener_shouldReturn200() throws Exception {
        when(thesisReportUseCase.obtenerPorId(1)).thenReturn(buildResponse(1));

        mockMvc.perform(get("/api/v1/thesis/reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInformeTesis").value(1));
    }

    @Test
    void obtener_shouldReturn404_whenNotFound() throws Exception {
        when(thesisReportUseCase.obtenerPorId(999)).thenThrow(new ThesisReportNotFoundException(999));

        mockMvc.perform(get("/api/v1/thesis/reports/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe el informe de tesis con id: 999"));
    }

    @Test
    void listarPorPlan_shouldReturn200() throws Exception {
        when(thesisReportUseCase.listarPorPlan(1)).thenReturn(List.of(buildResponse(1)));

        mockMvc.perform(get("/api/v1/thesis/reports/plan/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idInformeTesis").value(1));
    }

    @Test
    void aprobar_shouldReturn200() throws Exception {
        when(thesisReportUseCase.aprobarInforme(1)).thenReturn(buildResponse(1));

        mockMvc.perform(patch("/api/v1/thesis/reports/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInformeTesis").value(1));
    }

    @Test
    void observar_shouldReturn200() throws Exception {
        when(thesisReportUseCase.observarInforme(eq(1), anyString())).thenReturn(buildResponse(1));

        mockMvc.perform(patch("/api/v1/thesis/reports/1/observe?observacion=Corregir+formato"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInformeTesis").value(1));
    }

    @Test
    void observar_shouldReturn400_whenInvalidTransition() throws Exception {
        when(thesisReportUseCase.observarInforme(eq(1), anyString()))
                .thenThrow(new InvalidStateTransitionException("No se puede observar un informe aprobado"));

        mockMvc.perform(patch("/api/v1/thesis/reports/1/observe?observacion=test"))
                .andExpect(status().isBadRequest());
    }
}
