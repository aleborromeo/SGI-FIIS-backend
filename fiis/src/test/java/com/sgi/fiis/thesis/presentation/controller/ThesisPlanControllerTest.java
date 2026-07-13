package com.sgi.fiis.thesis.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.*;
import com.sgi.fiis.thesis.domain.exception.BusinessRuleViolationException;
import com.sgi.fiis.thesis.domain.exception.ThesisPlanNotFoundException;
import com.sgi.fiis.thesis.domain.exception.InvalidStateTransitionException;
import com.sgi.fiis.thesis.domain.port.in.ThesisPlanUseCase;
import com.sgi.fiis.thesis.presentation.handler.ThesisExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ThesisPlanControllerTest {

    private MockMvc mockMvc;
    private ThesisPlanUseCase thesisPlanUseCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        thesisPlanUseCase = mock(ThesisPlanUseCase.class);
        var controller = new ThesisPlanController(thesisPlanUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ThesisExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private ThesisPlanResponse buildResponse(Integer id) {
        return new ThesisPlanResponse(
                id, "Tesis de IA", "Resumen", 1L, 1, 1, 99,
                ThesisPlanStatus.POSTULADO, null, null, 500,
                ThesisProcedureStatus.PENDIENTE_COORDINADOR, ReviewerRole.COORDINADOR_GRUPO
        );
    }

    @Test
    void registrar_shouldReturn201() throws Exception {
        var command = new RegisterThesisPlanCommand("Tesis de IA", "Resumen", 1, 1, 99);
        var response = buildResponse(1);

        when(thesisPlanUseCase.registrarPlan(any(RegisterThesisPlanCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/thesis/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/thesis/plans/1"))
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }

    @Test
    void registrar_shouldReturn400_whenInvalidBody() throws Exception {
        var command = new RegisterThesisPlanCommand("", "", null, null, null);

        mockMvc.perform(post("/api/v1/thesis/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_shouldReturn400_whenBusinessRuleViolation() throws Exception {
        var command = new RegisterThesisPlanCommand("Tesis", "Resumen", 1, 1, 99);

        when(thesisPlanUseCase.registrarPlan(any())).thenThrow(new BusinessRuleViolationException("Grupo inactivo"));

        mockMvc.perform(post("/api/v1/thesis/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Grupo inactivo"));
    }

    @Test
    void obtener_shouldReturn200() throws Exception {
        when(thesisPlanUseCase.obtenerPorId(1)).thenReturn(buildResponse(1));

        mockMvc.perform(get("/api/v1/thesis/plans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }

    @Test
    void obtener_shouldReturn404_whenNotFound() throws Exception {
        when(thesisPlanUseCase.obtenerPorId(999)).thenThrow(new ThesisPlanNotFoundException(999));

        mockMvc.perform(get("/api/v1/thesis/plans/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No existe el plan de tesis con id: 999"));
    }

    @Test
    void listarPorEstudiante_shouldReturn200() throws Exception {
        when(thesisPlanUseCase.listarPorEstudiante(1L)).thenReturn(List.of(buildResponse(1)));

        mockMvc.perform(get("/api/v1/thesis/plans/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPlanTesis").value(1));
    }

    @Test
    void listarPorGrupo_shouldReturn200() throws Exception {
        when(thesisPlanUseCase.listarPorGrupo(1)).thenReturn(List.of(buildResponse(1)));

        mockMvc.perform(get("/api/v1/thesis/plans/group/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPlanTesis").value(1));
    }

    @Test
    void listarPendientes_shouldReturn200() throws Exception {
        when(thesisPlanUseCase.listarPendientesPorRevisor(ReviewerRole.COORDINADOR_GRUPO))
                .thenReturn(List.of(buildResponse(1)));

        mockMvc.perform(get("/api/v1/thesis/plans/pending?revisor=COORDINADOR_GRUPO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPlanTesis").value(1));
    }

    @Test
    void listarPendientes_shouldReturn400_whenInvalidRevisor() throws Exception {
        mockMvc.perform(get("/api/v1/thesis/plans/pending?revisor=INVALIDO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void aprobarCoordinador_shouldReturn200() throws Exception {
        when(thesisPlanUseCase.aprobarPorCoordinador(1)).thenReturn(buildResponse(1));

        mockMvc.perform(patch("/api/v1/thesis/plans/1/coordinator/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }

    @Test
    void observarCoordinador_shouldReturn200() throws Exception {
        var command = new ObserveThesisPlanCommand("Falta bibliografia", 100);
        when(thesisPlanUseCase.observarPorCoordinador(eq(1), any(ObserveThesisPlanCommand.class)))
                .thenReturn(buildResponse(1));

        mockMvc.perform(patch("/api/v1/thesis/plans/1/coordinator/observe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }

    @Test
    void rechazarCoordinador_shouldReturn200() throws Exception {
        when(thesisPlanUseCase.rechazarPorCoordinador(eq(1), anyString())).thenReturn(buildResponse(1));

        mockMvc.perform(patch("/api/v1/thesis/plans/1/coordinator/reject?motivo=Fuera+de+ambito"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }

    @Test
    void aprobarDirector_shouldReturn200() throws Exception {
        when(thesisPlanUseCase.aprobarPorDirector(1)).thenReturn(buildResponse(1));

        mockMvc.perform(patch("/api/v1/thesis/plans/1/director/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }

    @Test
    void observarDirector_shouldReturn200() throws Exception {
        var command = new ObserveThesisPlanCommand("Faltan firmas", 101);
        when(thesisPlanUseCase.observarPorDirector(eq(1), any(ObserveThesisPlanCommand.class)))
                .thenReturn(buildResponse(1));

        mockMvc.perform(patch("/api/v1/thesis/plans/1/director/observe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }

    @Test
    void rectify_shouldReturn200() throws Exception {
        var command = new RectifyThesisPlanCommand(101, "Nuevo resumen", "Corregido");
        when(thesisPlanUseCase.subsanarPlan(eq(1), any(RectifyThesisPlanCommand.class)))
                .thenReturn(buildResponse(1));

        mockMvc.perform(patch("/api/v1/thesis/plans/1/rectify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }

    @Test
    void registrarResolucion_shouldReturn200() throws Exception {
        var command = new RegisterResolutionCommand("RES-001", LocalDate.of(2026, 7, 13), "Aprobado", 200);
        when(thesisPlanUseCase.registrarResolucion(eq(1), any(RegisterResolutionCommand.class)))
                .thenReturn(buildResponse(1));

        mockMvc.perform(post("/api/v1/thesis/plans/1/dean/resolution")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPlanTesis").value(1));
    }
}
