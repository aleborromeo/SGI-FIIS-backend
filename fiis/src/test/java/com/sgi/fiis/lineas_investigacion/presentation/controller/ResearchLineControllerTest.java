package com.sgi.fiis.lineas_investigacion.presentation.controller;

import tools.jackson.databind.ObjectMapper;
import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineResponseDto;
import com.sgi.fiis.lineas_investigacion.application.usecase.*;
import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import com.sgi.fiis.lineas_investigacion.presentation.mapper.ResearchLineMapper;
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

@WebMvcTest(ResearchLineController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ResearchLineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterResearchLineUseCase registerResearchLineUseCase;

    @MockitoBean
    private ListResearchLinesUseCase listResearchLinesUseCase;

    @MockitoBean
    private GetResearchLineUseCase getResearchLineUseCase;

    @MockitoBean
    private ChangeResearchLineStatusUseCase changeResearchLineStatusUseCase;

    @MockitoBean
    private ResearchLineMapper mapper;

    @MockitoBean
    private com.sgi.fiis.auth.domain.port.TokenProviderPort tokenProviderPort;

    @MockitoBean
    private com.sgi.fiis.auth.infrastructure.security.CustomUserDetailsService customUserDetailsService;

    @Test
    void register_shouldReturn201_whenDataIsValid() throws Exception {
        ResearchLineRequestDto request = new ResearchLineRequestDto("Inteligencia Artificial");
        ResearchLine domain = ResearchLine.builder().id(1).lineName("Inteligencia Artificial").active(true).build();
        ResearchLineResponseDto response = ResearchLineResponseDto.builder()
                .id(1).lineName("Inteligencia Artificial").active(true).build();

        given(mapper.toDomain(any())).willReturn(domain);
        given(registerResearchLineUseCase.execute(any())).willReturn(domain);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(post("/api/v1/research-lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lineName").value("Inteligencia Artificial"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void register_shouldReturn400_whenNameIsEmpty() throws Exception {
        ResearchLineRequestDto request = new ResearchLineRequestDto("");

        mockMvc.perform(post("/api/v1/research-lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturn409_whenNameIsDuplicated() throws Exception {
        ResearchLineRequestDto request = new ResearchLineRequestDto("IA Duplicada");

        given(mapper.toDomain(any())).willReturn(ResearchLine.builder().lineName("IA Duplicada").build());
        given(registerResearchLineUseCase.execute(any()))
                .willThrow(new DuplicateResourceException("ResearchLine", "lineName", "IA Duplicada"));

        mockMvc.perform(post("/api/v1/research-lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void list_shouldReturn200_withLinesList() throws Exception {
        List<ResearchLine> domains = List.of(
                ResearchLine.builder().id(1).lineName("IA").active(true).build()
        );
        ResearchLineResponseDto dto = ResearchLineResponseDto.builder()
                .id(1).lineName("IA").active(true).build();

        given(listResearchLinesUseCase.execute(anyBoolean())).willReturn(domains);
        given(mapper.toResponseDto(any())).willReturn(dto);

        mockMvc.perform(get("/api/v1/research-lines"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].lineName").value("IA"));
    }

    @Test
    void get_shouldReturn404_whenDoesNotExist() throws Exception {
        given(getResearchLineUseCase.execute(99))
                .willThrow(new ResourceNotFoundException("ResearchLine", "id", 99));

        mockMvc.perform(get("/api/v1/research-lines/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void changeStatus_shouldReturn200_whenActivatesLine() throws Exception {
        ResearchLine line = ResearchLine.builder().id(1).active(true).build();
        ResearchLineResponseDto response = ResearchLineResponseDto.builder()
                .id(1).active(true).build();

        given(changeResearchLineStatusUseCase.execute(1, true)).willReturn(line);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(patch("/api/v1/research-lines/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void changeStatus_shouldReturn200_whenDeactivatesLine() throws Exception {
        ResearchLine line = ResearchLine.builder().id(1).active(false).build();
        ResearchLineResponseDto response = ResearchLineResponseDto.builder()
                .id(1).active(false).build();

        given(changeResearchLineStatusUseCase.execute(1, false)).willReturn(line);
        given(mapper.toResponseDto(any())).willReturn(response);

        mockMvc.perform(patch("/api/v1/research-lines/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"active\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
