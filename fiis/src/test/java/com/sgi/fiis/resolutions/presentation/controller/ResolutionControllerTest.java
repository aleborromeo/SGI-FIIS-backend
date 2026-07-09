package com.sgi.fiis.resolutions.presentation.controller;

import com.sgi.fiis.TestcontainersConfig;
import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionCommand;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ResolutionControllerTest extends TestcontainersConfig {

    private MockMvc mockMvc;

    @Mock
    private IssueResolutionUseCase issueResolutionUseCase;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ResolutionController resolutionController;

    private Resolution sampleResolution;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(resolutionController).build();

        sampleResolution = new Resolution(
                1L,
                "RES-2023-001",
                LocalDate.of(2023, 10, 1),
                "Thesis approval",
                10L,
                100L,
                LocalDateTime.of(2023, 10, 1, 10, 0)
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
}
