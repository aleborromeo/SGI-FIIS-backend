package com.sgi.fiis.evaluaciones.presentation.controller;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadorUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarEvaluacionesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.RegistrarResultadoEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EvaluacionControllerTest {

    private AsignarEvaluadorUseCase asignarEvaluadorUseCase;
    private RegistrarResultadoEvaluacionUseCase registrarResultadoEvaluacionUseCase;
    private ConsultarEvaluacionesUseCase consultarEvaluacionesUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        asignarEvaluadorUseCase = mock(AsignarEvaluadorUseCase.class);
        registrarResultadoEvaluacionUseCase = mock(RegistrarResultadoEvaluacionUseCase.class);
        consultarEvaluacionesUseCase = mock(ConsultarEvaluacionesUseCase.class);

        EvaluacionController controller = new EvaluacionController(
                asignarEvaluadorUseCase,
                registrarResultadoEvaluacionUseCase,
                consultarEvaluacionesUseCase
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void asignarEvaluadorDebeRetornarCreated() throws Exception {
        String requestJson = """
                {
                  "idProyecto": 1,
                  "idPlanTesis": null,
                  "idEvaluador": 2
                }
                """;

        EvaluacionResponse response = new EvaluacionResponse(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null,
                true
        );

        when(asignarEvaluadorUseCase.asignarEvaluador(any(AsignarEvaluadorCommand.class)))
                .thenReturn(response);

        mockMvc.perform(post("/evaluaciones/asignar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEvaluacion").value(1))
                .andExpect(jsonPath("$.idProyecto").value(1))
                .andExpect(jsonPath("$.idEvaluador").value(2))
                .andExpect(jsonPath("$.pendiente").value(true));

        ArgumentCaptor<AsignarEvaluadorCommand> captor =
                ArgumentCaptor.forClass(AsignarEvaluadorCommand.class);

        verify(asignarEvaluadorUseCase).asignarEvaluador(captor.capture());

        assertEquals(1L, captor.getValue().idProyecto());
        assertEquals(2L, captor.getValue().idEvaluador());
    }

    @Test
    void registrarResultadoDebeRetornarOk() throws Exception {
        String requestJson = """
                {
                  "idEvaluador": 2,
                  "resultado": "APROBADO",
                  "puntaje": 90,
                  "observaciones": "Cumple con los criterios establecidos."
                }
                """;

        EvaluacionResponse response = new EvaluacionResponse(
                1L,
                1L,
                null,
                2L,
                ResultadoEvaluacion.APROBADO,
                90,
                "Cumple con los criterios establecidos.",
                LocalDateTime.now(),
                LocalDateTime.now(),
                false
        );

        when(registrarResultadoEvaluacionUseCase.registrarResultado(any(RegistrarResultadoEvaluacionCommand.class)))
                .thenReturn(response);

        mockMvc.perform(post("/evaluaciones/1/resultado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvaluacion").value(1))
                .andExpect(jsonPath("$.resultado").value("APROBADO"))
                .andExpect(jsonPath("$.puntaje").value(90))
                .andExpect(jsonPath("$.pendiente").value(false));

        ArgumentCaptor<RegistrarResultadoEvaluacionCommand> captor =
                ArgumentCaptor.forClass(RegistrarResultadoEvaluacionCommand.class);

        verify(registrarResultadoEvaluacionUseCase).registrarResultado(captor.capture());

        assertEquals(1L, captor.getValue().idEvaluacion());
        assertEquals(2L, captor.getValue().idEvaluador());
        assertEquals(ResultadoEvaluacion.APROBADO, captor.getValue().resultado());
    }

    @Test
    void listarTodasDebeRetornarOk() throws Exception {
        EvaluacionResponse response = new EvaluacionResponse(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null,
                true
        );

        when(consultarEvaluacionesUseCase.listarTodas())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/evaluaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEvaluacion").value(1))
                .andExpect(jsonPath("$[0].idEvaluador").value(2));
    }

    @Test
    void buscarPorIdDebeRetornarOk() throws Exception {
        EvaluacionResponse response = new EvaluacionResponse(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null,
                true
        );

        when(consultarEvaluacionesUseCase.buscarPorId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/evaluaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvaluacion").value(1))
                .andExpect(jsonPath("$.idProyecto").value(1));
    }

    @Test
    void listarPorEvaluadorDebeRetornarOk() throws Exception {
        EvaluacionResponse response = new EvaluacionResponse(
                1L,
                1L,
                null,
                2L,
                null,
                null,
                null,
                LocalDateTime.now(),
                null,
                true
        );

        when(consultarEvaluacionesUseCase.listarPorEvaluador(2L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/evaluaciones/evaluador/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEvaluacion").value(1))
                .andExpect(jsonPath("$[0].idEvaluador").value(2));
    }
}