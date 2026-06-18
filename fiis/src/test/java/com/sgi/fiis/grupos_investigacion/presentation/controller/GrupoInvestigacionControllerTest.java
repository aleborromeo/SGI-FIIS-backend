package com.sgi.fiis.grupos_investigacion.presentation.controller;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.grupos_investigacion.application.dto.*;
import com.sgi.fiis.grupos_investigacion.application.usecase.*;
import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;
import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;
import com.sgi.fiis.grupos_investigacion.presentation.mapper.GrupoInvestigacionMapper;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionResponseDto;
import com.sgi.fiis.lineas_investigacion.application.usecase.ListarLineasPorGrupoUseCase;
import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.LineaInvestigacionMapper;
import com.sgi.fiis.shared.domain.exception.BusinessException;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GrupoInvestigacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class GrupoInvestigacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private CrearGrupoUseCase crearGrupoUseCase;
    @MockitoBean private com.sgi.fiis.auth.domain.port.TokenProviderPort tokenProviderPort;
    @MockitoBean private com.sgi.fiis.auth.infrastructure.security.CustomUserDetailsService customUserDetailsService;
    @MockitoBean private ListarGruposUseCase listarGruposUseCase;
    @MockitoBean private ObtenerGrupoUseCase obtenerGrupoUseCase;
    @MockitoBean private AsignarCoordinadorUseCase asignarCoordinadorUseCase;
    @MockitoBean private AsignarMiembroUseCase asignarMiembroUseCase;
    @MockitoBean private RetirarMiembroUseCase retirarMiembroUseCase;
    @MockitoBean private ListarMiembrosUseCase listarMiembrosUseCase;
    @MockitoBean private ListarLineasPorGrupoUseCase listarLineasPorGrupoUseCase;
    @MockitoBean private GrupoInvestigacionMapper mapper;
    @MockitoBean private LineaInvestigacionMapper lineaMapper;

    @Test
    void crear_deberiaRetornar201_cuandoDatosValidos() throws Exception {
        GrupoInvestigacionRequestDto request = new GrupoInvestigacionRequestDto("GI-001", "Grupo de IA");
        GrupoInvestigacion domain = GrupoInvestigacion.builder()
                .id(1).codigoGrupo("GI-001").nombreGrupo("Grupo de IA").esActivo(true).build();
        GrupoInvestigacionResponseDto response = GrupoInvestigacionResponseDto.builder()
                .id(1).codigoGrupo("GI-001").nombreGrupo("Grupo de IA").esActivo(true).build();

        given(mapper.toDomain(any())).willReturn(domain);
        given(crearGrupoUseCase.execute(any())).willReturn(domain);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/grupos-investigacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigoGrupo").value("GI-001"))
                .andExpect(jsonPath("$.esActivo").value(true));
    }

    @Test
    void crear_deberiaRetornar400_cuandoCodigoVacio() throws Exception {
        GrupoInvestigacionRequestDto request = new GrupoInvestigacionRequestDto("", "Grupo de IA");

        mockMvc.perform(post("/api/v1/grupos-investigacion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_deberiaRetornar200_conListaDeGrupos() throws Exception {
        List<GrupoInvestigacion> grupos = List.of(
                GrupoInvestigacion.builder().id(1).codigoGrupo("GI-001").nombreGrupo("Grupo A").build()
        );
        GrupoInvestigacionResponseDto dto = GrupoInvestigacionResponseDto.builder()
                .id(1).codigoGrupo("GI-001").nombreGrupo("Grupo A").build();

        given(listarGruposUseCase.execute()).willReturn(grupos);
        given(mapper.toResponseDto(any())).willReturn(dto);

        mockMvc.perform(get("/api/v1/grupos-investigacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].codigoGrupo").value("GI-001"));
    }

    @Test
    void obtener_deberiaRetornar404_cuandoGrupoNoExiste() throws Exception {
        given(obtenerGrupoUseCase.execute(99))
                .willThrow(new ResourceNotFoundException("GrupoInvestigacion", "id", 99));

        mockMvc.perform(get("/api/v1/grupos-investigacion/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void asignarCoordinador_deberiaRetornar200_cuandoDatosValidos() throws Exception {
        AsignarCoordinadorRequestDto request = new AsignarCoordinadorRequestDto(3);
        GrupoInvestigacion actualizado = GrupoInvestigacion.builder()
                .id(1).idCoordinadorActual(3).esActivo(true).build();
        GrupoInvestigacionResponseDto response = GrupoInvestigacionResponseDto.builder()
                .id(1).idCoordinadorActual(3).esActivo(true).build();

        given(asignarCoordinadorUseCase.execute(1, 3)).willReturn(actualizado);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(patch("/api/v1/grupos-investigacion/1/coordinador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCoordinadorActual").value(3));
    }

    @Test
    void asignarMiembro_deberiaRetornar400_cuandoViolaRF21() throws Exception {
        AsignarMiembroRequestDto request = new AsignarMiembroRequestDto(5);

        given(asignarMiembroUseCase.execute(eq(1), eq(5)))
                .willThrow(new BusinessException("El usuario con id 5 ya pertenece a un grupo de investigación activo (RF-21)"));

        mockMvc.perform(post("/api/v1/grupos-investigacion/1/miembros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void asignarMiembro_deberiaRetornar201_cuandoDatosValidos() throws Exception {
        AsignarMiembroRequestDto request = new AsignarMiembroRequestDto(2);
        Membresia membresia = Membresia.builder()
                .id(10).idGrupo(1).idUsuario(2).esActivo(true)
                .fechaInicio(LocalDateTime.now()).build();
        MembresiaResponseDto response = MembresiaResponseDto.builder()
                .id(10).idGrupo(1).idUsuario(2).esActivo(true).build();

        given(asignarMiembroUseCase.execute(1, 2)).willReturn(membresia);
        given(mapper.toMembresiaResponseDto(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/grupos-investigacion/1/miembros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idGrupo").value(1))
                .andExpect(jsonPath("$.idUsuario").value(2));
    }

    @Test
    void retirarMiembro_deberiaRetornar200_cuandoMiembroActivo() throws Exception {
        Membresia retirada = Membresia.builder()
                .id(10).idGrupo(1).idUsuario(5).esActivo(false)
                .fechaFin(LocalDateTime.now()).build();
        MembresiaResponseDto response = MembresiaResponseDto.builder()
                .id(10).esActivo(false).build();

        given(retirarMiembroUseCase.execute(1, 5)).willReturn(retirada);
        given(mapper.toMembresiaResponseDto(any())).willReturn(response);

        mockMvc.perform(delete("/api/v1/grupos-investigacion/1/miembros/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.esActivo").value(false));
    }

    @Test
    void listarLineas_deberiaRetornar200_conLineasDelGrupo() throws Exception {
        GrupoInvestigacion grupo = GrupoInvestigacion.builder().id(1).esActivo(true).build();
        List<LineaInvestigacion> lineas = List.of(
                LineaInvestigacion.builder().id(2).nombreLinea("Robótica").build()
        );
        LineaInvestigacionResponseDto lineaDto = LineaInvestigacionResponseDto.builder()
                .id(2).nombreLinea("Robótica").esActiva(true).build();

        given(obtenerGrupoUseCase.execute(1)).willReturn(grupo);
        given(listarLineasPorGrupoUseCase.execute(1)).willReturn(lineas);
        given(lineaMapper.toResponseDto(any())).willReturn(lineaDto);

        mockMvc.perform(get("/api/v1/grupos-investigacion/1/lineas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].nombreLinea").value("Robótica"));
    }
}
