package com.sgi.fiis.resolutions.presentation.controller;

import com.sgi.fiis.resolutions.application.dto.ResolutionResponseDTO;
import com.sgi.fiis.resolutions.application.usecase.GetResolutionUseCase;
import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionCommand;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ResolutionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IssueResolutionUseCase issueResolutionUseCase;

    @Mock
    private GetResolutionUseCase getResolutionUseCase;

    @Mock
    private MessageSource messageSource;

    private ResolutionController resolutionController;

    private Resolution sampleResolution;

    @BeforeEach
    void setUp() {
        resolutionController = new ResolutionController(issueResolutionUseCase, getResolutionUseCase, messageSource);
        mockMvc = MockMvcBuilders.standaloneSetup(resolutionController).build();

        sampleResolution = new Resolution(
                1L,
                "RES-2023-001",
                LocalDate.of(2023, Month.OCTOBER, 1),
                "Thesis approval",
                10L,
                100L,
                LocalDateTime.of(2023, Month.OCTOBER, 1, 10, 0)
        );
    }

    @Test
    void issueResolution_shouldReturn201_whenValid() throws Exception {
        when(issueResolutionUseCase.issue(any(IssueResolutionCommand.class))).thenReturn(sampleResolution);
        when(messageSource.getMessage(eq("resolution.issue.success"), any(), any(Locale.class)))
                .thenReturn("Resolution issued successfully.");

        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/resolutions")
                        .file(file)
                        .param("numeroResolucion", "RES-2023-001")
                        .param("fechaEmision", "2023-10-01")
                        .param("asunto", "Thesis approval")
                        .param("idTramite", "10")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Resolution issued successfully."))
                .andExpect(jsonPath("$.data.idResolucion").value(1))
                .andExpect(jsonPath("$.data.numeroResolucion").value("RES-2023-001"))
                .andExpect(jsonPath("$.data.asunto").value("Thesis approval"))
                .andExpect(jsonPath("$.data.idTramite").value(10));
    }

    @Test
    void issueResolution_shouldReturn403_whenNotDecano() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/resolutions")
                        .file(file)
                        .param("numeroResolucion", "RES-2023-001")
                        .param("fechaEmision", "2023-10-01")
                        .param("asunto", "Thesis approval")
                        .param("idTramite", "10")
                        .with(user("student@unas.edu.pe").roles("ESTUDIANTE"))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
    }

    @Test
    void issueResolution_shouldReturn401_whenUnauthenticated() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "archivo",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Test PDF content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/resolutions")
                        .file(file)
                        .param("numeroResolucion", "RES-2023-001")
                        .param("fechaEmision", "2023-10-01")
                        .param("asunto", "Thesis approval")
                        .param("idTramite", "10")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getResolution_shouldReturn200_whenFound() throws Exception {
        ResolutionResponseDTO responseDTO = new ResolutionResponseDTO(
                1L,
                "RES-2023-001",
                LocalDate.of(2023, Month.OCTOBER, 1),
                "Thesis approval",
                10L,
                100L,
                LocalDateTime.of(2023, Month.OCTOBER, 1, 10, 0)
        );

        when(getResolutionUseCase.execute(1L)).thenReturn(Optional.of(responseDTO));

        mockMvc.perform(get("/api/v1/resolutions/1")
                        .with(user("user@unas.edu.pe").roles("DOCENTE_INVESTIGADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idResolucion").value(1))
                .andExpect(jsonPath("$.numeroResolucion").value("RES-2023-001"))
                .andExpect(jsonPath("$.asunto").value("Thesis approval"));
    }

    @Test
    void getResolution_shouldReturn404_whenNotFound() throws Exception {
        when(getResolutionUseCase.execute(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/resolutions/999")
                        .with(user("user@unas.edu.pe").roles("DOCENTE_INVESTIGADOR")))
                .andExpect(status().isNotFound());
    }
}
