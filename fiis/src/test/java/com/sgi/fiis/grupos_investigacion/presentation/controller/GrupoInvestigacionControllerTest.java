package com.sgi.fiis.grupos_investigacion.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgi.fiis.grupos_investigacion.application.dto.*;
import com.sgi.fiis.grupos_investigacion.application.usecase.*;
import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.presentation.mapper.GrupoInvestigacionMapper;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListarLineasPorGrupoUseCase;
import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionResponseDto;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.LineaInvestigacionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GrupoInvestigacionControllerTest {

    private MockMvc mockMvc;
    private CrearGrupoUseCase crearGrupoUseCase;
    private ListarGruposUseCase listarGruposUseCase;
    private ObtenerGrupoUseCase obtenerGrupoUseCase;
    private AsignarCoordinadorUseCase asignarCoordinadorUseCase;
    private AsignarMiembroUseCase asignarMiembroUseCase;
    private RetirarMiembroUseCase retirarMiembroUseCase;
    private ListarMiembrosUseCase listarMiembrosUseCase;
    private ListarLineasPorGrupoUseCase listarLineasPorGrupoUseCase;
    private GrupoInvestigacionMapper mapper;
    private LineaInvestigacionMapper lineaMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        crearGrupoUseCase = mock(CrearGrupoUseCase.class);
        listarGruposUseCase = mock(ListarGruposUseCase.class);
        obtenerGrupoUseCase = mock(ObtenerGrupoUseCase.class);
        asignarCoordinadorUseCase = mock(AsignarCoordinadorUseCase.class);
        asignarMiembroUseCase = mock(AsignarMiembroUseCase.class);
        retirarMiembroUseCase = mock(RetirarMiembroUseCase.class);
        listarMiembrosUseCase = mock(ListarMiembrosUseCase.class);
        listarLineasPorGrupoUseCase = mock(ListarLineasPorGrupoUseCase.class);
        mapper = mock(GrupoInvestigacionMapper.class);
        lineaMapper = mock(LineaInvestigacionMapper.class);
        GrupoInvestigacionController controller = new GrupoInvestigacionController(
                crearGrupoUseCase, listarGruposUseCase, obtenerGrupoUseCase,
                asignarCoordinadorUseCase, asignarMiembroUseCase, retirarMiembroUseCase,
                listarMiembrosUseCase, listarLineasPorGrupoUseCase, mapper, lineaMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void shouldCrearGrupo() throws Exception {
        GrupoInvestigacionRequestDto dto = new GrupoInvestigacionRequestDto();
        dto.setCodigoGrupo("GIN-001");
        dto.setNombreGrupo("Grupo Test");

        GrupoInvestigacion domain = GrupoInvestigacion.builder().codigoGrupo("GIN-001").build();
        GrupoInvestigacion saved = GrupoInvestigacion.builder().id(1).codigoGrupo("GIN-001").build();
        GrupoInvestigacionResponseDto response = GrupoInvestigacionResponseDto.builder().id(1).codigoGrupo("GIN-001")
                .build();

        when(mapper.toDomain(any(GrupoInvestigacionRequestDto.class))).thenReturn(domain);
        when(crearGrupoUseCase.execute(any(GrupoInvestigacion.class))).thenReturn(saved);
        when(mapper.toResponseDto(any(GrupoInvestigacion.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/grupos-investigacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldListarGrupos() throws Exception {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        GrupoInvestigacionResponseDto response = GrupoInvestigacionResponseDto.builder().id(1).build();

        when(listarGruposUseCase.execute()).thenReturn(Collections.singletonList(grupo));
        when(mapper.toResponseDto(any(GrupoInvestigacion.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/grupos-investigacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldObtenerGrupo() throws Exception {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).build();
        GrupoInvestigacionResponseDto response = GrupoInvestigacionResponseDto.builder().id(1).build();

        when(obtenerGrupoUseCase.execute(1)).thenReturn(grupo);
        when(mapper.toResponseDto(any(GrupoInvestigacion.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/grupos-investigacion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldAsignarCoordinador() throws Exception {
        AsignarCoordinadorRequestDto dto = new AsignarCoordinadorRequestDto();
        dto.setIdUsuario(5);

        GrupoInvestigacion domain = GrupoInvestigacion.builder().id(1).idCoordinadorActual(5).build();
        GrupoInvestigacionResponseDto response = GrupoInvestigacionResponseDto.builder().id(1).idCoordinadorActual(5)
                .build();

        when(asignarCoordinadorUseCase.execute(1, 5)).thenReturn(domain);
        when(mapper.toResponseDto(any(GrupoInvestigacion.class))).thenReturn(response);

        mockMvc.perform(patch("/api/v1/grupos-investigacion/1/coordinador")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idCoordinadorActual").value(5));
    }

    @Test
    void shouldAsignarMiembro() throws Exception {
        AsignarMiembroRequestDto dto = new AsignarMiembroRequestDto();
        dto.setIdUsuario(5);

        Membresia domain = Membresia.builder().id(1).idGrupo(2).idUsuario(5).build();
        MembresiaResponseDto response = MembresiaResponseDto.builder().id(1).idGrupo(2).idUsuario(5).build();

        when(asignarMiembroUseCase.execute(2, 5)).thenReturn(domain);
        when(mapper.toMembresiaResponseDto(any(Membresia.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/grupos-investigacion/2/miembros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idUsuario").value(5));
    }

    @Test
    void shouldRetirarMiembro() throws Exception {
        Membresia domain = Membresia.builder().id(1).idGrupo(2).idUsuario(5).esActivo(false).build();
        MembresiaResponseDto response = MembresiaResponseDto.builder().id(1).idGrupo(2).idUsuario(5).esActivo(false)
                .build();

        when(retirarMiembroUseCase.execute(2, 5)).thenReturn(domain);
        when(mapper.toMembresiaResponseDto(any(Membresia.class))).thenReturn(response);

        mockMvc.perform(delete("/api/v1/grupos-investigacion/2/miembros/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.esActivo").value(false));
    }

    @Test
    void shouldListarMiembros() throws Exception {
        Membresia domain = Membresia.builder().id(1).build();
        MembresiaResponseDto response = MembresiaResponseDto.builder().id(1).build();

        when(listarMiembrosUseCase.execute(2)).thenReturn(Collections.singletonList(domain));
        when(mapper.toMembresiaResponseDto(any(Membresia.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/grupos-investigacion/2/miembros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldListarLineas() throws Exception {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(2).build();
        LineaInvestigacion line = LineaInvestigacion.builder().id(10).build();
        LineaInvestigacionResponseDto response = LineaInvestigacionResponseDto.builder().id(10).build();

        when(obtenerGrupoUseCase.execute(2)).thenReturn(grupo);
        when(listarLineasPorGrupoUseCase.execute(2)).thenReturn(Collections.singletonList(line));
        when(lineaMapper.toResponseDto(any(LineaInvestigacion.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/grupos-investigacion/2/lineas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }
}
