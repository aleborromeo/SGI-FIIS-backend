package com.sgi.fiis.resolutions.presentation.controller;

import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionCommand;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResolutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IssueResolutionUseCase issueResolutionUseCase;

    @MockitoBean
    private MessageSource messageSource;

    private Resolution sampleResolution;

    @BeforeEach
    void setUp() {
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
    void issueResolution_shouldReturn201_whenValidAndDecano() throws Exception {
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
                        .with(user("decano@unas.edu.pe").roles("DECANO"))
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
}
