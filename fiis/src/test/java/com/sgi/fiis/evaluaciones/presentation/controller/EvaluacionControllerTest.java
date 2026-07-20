package com.sgi.fiis.evaluaciones.presentation.controller;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluadorDisponibleResponse;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadorUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadoresUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarDetalleAnonimoUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarEvaluacionesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.EvaluarEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ListarEvaluadoresDisponiblesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.RegistrarResultadoEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.domain.enums.ResultadoEvaluacion;
import com.sgi.fiis.evaluaciones.presentation.dto.AnonymousProjectDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EvaluacionControllerTest {

    private static final String ID_EVALUACION_JSON_PATH = "$.idEvaluacion";
    private static final String ID_EVALUACION_ARRAY_JSON_PATH = "$[0].idEvaluacion";

    private AsignarEvaluadorUseCase asignarEvaluadorUseCase;
    private AsignarEvaluadoresUseCase asignarEvaluadoresUseCase;
    private RegistrarResultadoEvaluacionUseCase registrarResultadoEvaluacionUseCase;
    private ConsultarEvaluacionesUseCase consultarEvaluacionesUseCase;
    private EvaluarEvaluacionUseCase evaluarEvaluacionUseCase;
    private ConsultarDetalleAnonimoUseCase consultarDetalleAnonimoUseCase;
    private ListarEvaluadoresDisponiblesUseCase listarEvaluadoresDisponiblesUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        asignarEvaluadorUseCase = mock(AsignarEvaluadorUseCase.class);
        asignarEvaluadoresUseCase = mock(AsignarEvaluadoresUseCase.class);
        registrarResultadoEvaluacionUseCase = mock(RegistrarResultadoEvaluacionUseCase.class);
        consultarEvaluacionesUseCase = mock(ConsultarEvaluacionesUseCase.class);
        evaluarEvaluacionUseCase = mock(EvaluarEvaluacionUseCase.class);
        consultarDetalleAnonimoUseCase = mock(ConsultarDetalleAnonimoUseCase.class);
        listarEvaluadoresDisponiblesUseCase = mock(ListarEvaluadoresDisponiblesUseCase.class);

        EvaluacionController controller = new EvaluacionController(
                asignarEvaluadorUseCase,
                asignarEvaluadoresUseCase,
                registrarResultadoEvaluacionUseCase,
                consultarEvaluacionesUseCase,
                evaluarEvaluacionUseCase,
                consultarDetalleAnonimoUseCase,
                listarEvaluadoresDisponiblesUseCase
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
                LocalDateTime.now(ZoneId.of("UTC")),
                null,
                true
        );

        when(asignarEvaluadorUseCase.asignarEvaluador(any(AsignarEvaluadorCommand.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/evaluaciones/asignar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(ID_EVALUACION_JSON_PATH).value(1))
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
                LocalDateTime.now(ZoneId.of("UTC")),
                LocalDateTime.now(ZoneId.of("UTC")),
                false
        );

        when(registrarResultadoEvaluacionUseCase.registrarResultado(any(RegistrarResultadoEvaluacionCommand.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/evaluaciones/1/resultado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_EVALUACION_JSON_PATH).value(1))
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
                LocalDateTime.now(ZoneId.of("UTC")),
                null,
                true
        );

        when(consultarEvaluacionesUseCase.listarTodas())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/evaluaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_EVALUACION_ARRAY_JSON_PATH).value(1))
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
                LocalDateTime.now(ZoneId.of("UTC")),
                null,
                true
        );

        when(consultarEvaluacionesUseCase.buscarPorId(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/evaluaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_EVALUACION_JSON_PATH).value(1))
                .andExpect(jsonPath("$.idProyecto").value(1));
    }

    @Test
    void asignarEvaluadoresDebeRetornarCreated() throws Exception {
        String requestJson = """
                {
                  "projectId": 1,
                  "planTesisId": null,
                  "evaluadorIds": [2, 3]
                }
                """;

        EvaluacionResponse response = new EvaluacionResponse(
                1L, 1L, null, 2L, null, null, null, LocalDateTime.now(ZoneId.of("UTC")), null, true
        );

        when(asignarEvaluadoresUseCase.asignarEvaluadores(anyLong(), any(), anyList()))
                .thenReturn(List.of(response));

        mockMvc.perform(post("/api/v1/evaluaciones/asignar-multiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(ID_EVALUACION_ARRAY_JSON_PATH).value(1))
                .andExpect(jsonPath("$[0].idProyecto").value(1));
    }

    @Test
    void evaluarDebeRetornarOk() throws Exception {
        String requestJson = """
                {
                  "evaluatorId": 2,
                  "criteriaScores": [],
                  "totalScore": 85,
                  "observations": "Bueno",
                  "recommendations": "Aprobar",
                  "dictamen": "APROBADO"
                }
                """;

        EvaluacionResponse response = new EvaluacionResponse(
                1L, 1L, null, 2L, ResultadoEvaluacion.APROBADO, 85, "Bueno",
                LocalDateTime.now(ZoneId.of("UTC")), LocalDateTime.now(ZoneId.of("UTC")), false
        );

        when(evaluarEvaluacionUseCase.evaluar(anyLong(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/evaluaciones/1/evaluar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_EVALUACION_JSON_PATH).value(1))
                .andExpect(jsonPath("$.resultado").value("APROBADO"))
                .andExpect(jsonPath("$.puntaje").value(85));
    }

    @Test
    void consultarDetalleAnonimoDebeRetornarOk() throws Exception {
        var placeholder = AnonymousProjectDetailResponse.placeholder("PROY-1");

        when(consultarDetalleAnonimoUseCase.consultarDetalleAnonimo(1L))
                .thenReturn(placeholder);

        mockMvc.perform(get("/api/v1/evaluaciones/1/detalle-anonimo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expedienteCode").value("PROY-1"))
                .andExpect(jsonPath("$.criterios.length()").value(4))
                .andExpect(jsonPath("$.presupuestoTotal").value(50000.0));
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
                LocalDateTime.now(ZoneId.of("UTC")),
                null,
                true
        );

        when(consultarEvaluacionesUseCase.listarPorEvaluador(2L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/evaluaciones/evaluador/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ID_EVALUACION_ARRAY_JSON_PATH).value(1))
                .andExpect(jsonPath("$[0].idEvaluador").value(2));
    }

    @Test
    void listarEvaluadoresDisponiblesDebeRetornarOk() throws Exception {
        EvaluadorDisponibleResponse response = new EvaluadorDisponibleResponse(
                2L, "Pedro", "Gomez", "pedro@sgi.com", "EVALUADOR", "Rol Evaluador"
        );

        when(listarEvaluadoresDisponiblesUseCase.execute(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/evaluaciones/available-evaluators?projectId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].firstNames").value("Pedro"));
    }

    @Test
    void listarEvaluadoresPorProyectoDebeRetornarOk() throws Exception {
        com.sgi.fiis.evaluaciones.application.dto.response.EvaluadorAsignadoResponse response =
                new com.sgi.fiis.evaluaciones.application.dto.response.EvaluadorAsignadoResponse(
                        2L, "Pedro", "Gomez", "pedro@sgi.com", "EVALUADOR", "APROBADO", false
                );

        when(consultarEvaluacionesUseCase.listarEvaluadoresPorProyecto(1L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/evaluaciones/project/1/evaluators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$[0].nombres").value("Pedro"));
    }
}