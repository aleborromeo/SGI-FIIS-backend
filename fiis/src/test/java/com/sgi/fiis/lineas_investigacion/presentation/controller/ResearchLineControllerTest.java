package com.sgi.fiis.lineas_investigacion.presentation.controller;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionResponseDto;
import com.sgi.fiis.lineas_investigacion.application.usecase.*;
import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.LineaInvestigacionMapper;
import com.sgi.fiis.shared.domain.exception.DuplicateResourceException;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import com.sgi.fiis.shared.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LineaInvestigacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class LineaInvestigacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegistrarLineaUseCase registrarLineaUseCase;

    @MockitoBean
    private com.sgi.fiis.auth.domain.port.TokenProviderPort tokenProviderPort;

    @MockitoBean
    private com.sgi.fiis.auth.infrastructure.security.CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private ListarLineasUseCase listarLineasUseCase;

    @MockitoBean
    private ObtenerLineaUseCase obtenerLineaUseCase;

    @MockitoBean
    private CambiarEstadoLineaUseCase cambiarEstadoLineaUseCase;

    @MockitoBean
    private LineaInvestigacionMapper mapper;

    @MockitoBean
    private com.sgi.fiis.auth.domain.port.TokenProviderPort tokenProviderPort;

    @MockitoBean
    private com.sgi.fiis.auth.infrastructure.security.CustomUserDetailsService customUserDetailsService;

    @Test
    void registrar_deberiaRetornar201_cuandoDatosValidos() throws Exception {
        LineaInvestigacionRequestDto request = new LineaInvestigacionRequestDto("Inteligencia Artificial");
        LineaInvestigacion domain = LineaInvestigacion.builder().id(1).nombreLinea("Inteligencia Artificial").esActiva(true).build();
        LineaInvestigacionResponseDto response = LineaInvestigacionResponseDto.builder()
                .id(1).nombreLinea("Inteligencia Artificial").esActiva(true).build();

        given(mapper.toDomain(any())).willReturn(domain);
        given(registrarLineaUseCase.execute(any())).willReturn(domain);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/lineas-investigacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreLinea").value("Inteligencia Artificial"))
                .andExpect(jsonPath("$.esActiva").value(true));
    }

    @Test
    void registrar_deberiaRetornar400_cuandoNombreVacio() throws Exception {
        LineaInvestigacionRequestDto request = new LineaInvestigacionRequestDto("");

        mockMvc.perform(post("/api/v1/lineas-investigacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrar_deberiaRetornar409_cuandoNombreDuplicado() throws Exception {
        LineaInvestigacionRequestDto request = new LineaInvestigacionRequestDto("IA Duplicada");

        given(mapper.toDomain(any())).willReturn(LineaInvestigacion.builder().nombreLinea("IA Duplicada").build());
        given(registrarLineaUseCase.execute(any()))
                .willThrow(new DuplicateResourceException("LineaInvestigacion", "nombreLinea", "IA Duplicada"));

        mockMvc.perform(post("/api/v1/lineas-investigacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void listar_deberiaRetornar200_conListaDeLineas() throws Exception {
        List<LineaInvestigacion> dominios = List.of(
                LineaInvestigacion.builder().id(1).nombreLinea("IA").esActiva(true).build()
        );
        LineaInvestigacionResponseDto dto = LineaInvestigacionResponseDto.builder()
                .id(1).nombreLinea("IA").esActiva(true).build();

        given(listarLineasUseCase.execute(anyBoolean())).willReturn(dominios);
        given(mapper.toResponseDto(any())).willReturn(dto);

        mockMvc.perform(get("/api/v1/lineas-investigacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombreLinea").value("IA"));
    }

    @Test
    void obtener_deberiaRetornar404_cuandoNoExiste() throws Exception {
        given(obtenerLineaUseCase.execute(99))
                .willThrow(new ResourceNotFoundException("LineaInvestigacion", "id", 99));

        mockMvc.perform(get("/api/v1/lineas-investigacion/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cambiarEstado_deberiaRetornar200_cuandoActivaLinea() throws Exception {
        LineaInvestigacion linea = LineaInvestigacion.builder().id(1).esActiva(true).build();
        LineaInvestigacionResponseDto response = LineaInvestigacionResponseDto.builder()
                .id(1).esActiva(true).build();

        given(cambiarEstadoLineaUseCase.execute(1, true)).willReturn(linea);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(patch("/api/v1/lineas-investigacion/1/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"esActiva\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esActiva").value(true));
    }
}
