package com.sgi.fiis.lineas_investigacion.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionResponseDto;
import com.sgi.fiis.lineas_investigacion.application.usecase.CambiarEstadoLineaUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListarLineasUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.ObtenerLineaUseCase;
import com.sgi.fiis.lineas_investigacion.application.usecase.RegistrarLineaUseCase;
import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.LineaInvestigacionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LineaInvestigacionControllerTest {

    private MockMvc mockMvc;
    private RegistrarLineaUseCase registrarLineaUseCase;
    private ListarLineasUseCase listarLineasUseCase;
    private ObtenerLineaUseCase obtenerLineaUseCase;
    private CambiarEstadoLineaUseCase cambiarEstadoLineaUseCase;
    private LineaInvestigacionMapper mapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        registrarLineaUseCase = mock(RegistrarLineaUseCase.class);
        listarLineasUseCase = mock(ListarLineasUseCase.class);
        obtenerLineaUseCase = mock(ObtenerLineaUseCase.class);
        cambiarEstadoLineaUseCase = mock(CambiarEstadoLineaUseCase.class);
        mapper = mock(LineaInvestigacionMapper.class);
        LineaInvestigacionController controller = new LineaInvestigacionController(
                registrarLineaUseCase, listarLineasUseCase, obtenerLineaUseCase, cambiarEstadoLineaUseCase, mapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testRegistrarLinea() throws Exception {
        LineaInvestigacionRequestDto request = new LineaInvestigacionRequestDto();
        request.setNombreLinea("Test Linea");
        LineaInvestigacion domain = LineaInvestigacion.builder().nombreLinea("Test Linea").build();
        LineaInvestigacion saved = LineaInvestigacion.builder().id(1).nombreLinea("Test Linea").esActiva(true).build();
        LineaInvestigacionResponseDto response = LineaInvestigacionResponseDto.builder()
                .id(1)
                .nombreLinea("Test Linea")
                .esActiva(true)
                .build();

        when(mapper.toDomain(any(LineaInvestigacionRequestDto.class))).thenReturn(domain);
        when(registrarLineaUseCase.execute(any(LineaInvestigacion.class))).thenReturn(saved);
        when(mapper.toResponseDto(any(LineaInvestigacion.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/lineas-investigacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreLinea").value("Test Linea"));
    }

    @Test
    void testListarLineas() throws Exception {
        LineaInvestigacion linea = LineaInvestigacion.builder().id(1).nombreLinea("Test").build();
        LineaInvestigacionResponseDto response = LineaInvestigacionResponseDto.builder().id(1).nombreLinea("Test").build();

        when(listarLineasUseCase.execute(false)).thenReturn(Collections.singletonList(linea));
        when(mapper.toResponseDto(any(LineaInvestigacion.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/lineas-investigacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testObtenerLinea() throws Exception {
        LineaInvestigacion linea = LineaInvestigacion.builder().id(1).nombreLinea("Test").build();
        LineaInvestigacionResponseDto response = LineaInvestigacionResponseDto.builder().id(1).nombreLinea("Test").build();

        when(obtenerLineaUseCase.execute(1)).thenReturn(linea);
        when(mapper.toResponseDto(any(LineaInvestigacion.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/lineas-investigacion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCambiarEstado() throws Exception {
        LineaInvestigacion linea = LineaInvestigacion.builder().id(1).nombreLinea("Test").esActiva(false).build();
        LineaInvestigacionResponseDto response = LineaInvestigacionResponseDto.builder().id(1).esActiva(true).build();

        when(cambiarEstadoLineaUseCase.execute(1, true)).thenReturn(linea);
        when(mapper.toResponseDto(any(LineaInvestigacion.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/lineas-investigacion/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("esActiva", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esActiva").value(true));
    }
}
