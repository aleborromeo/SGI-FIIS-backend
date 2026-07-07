package com.sgi.fiis.reportes_progresivos.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgi.fiis.reportes_progresivos.ProgressReportTestHelper;
import com.sgi.fiis.reportes_progresivos.application.dto.AmendReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.CreateReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportType;
import com.sgi.fiis.reportes_progresivos.domain.port.in.AmendProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.CreateProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.QueryProgressReportUseCase;
import com.sgi.fiis.reportes_progresivos.domain.port.in.ReviewProgressReportUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressReportController Unit Tests")
class ProgressReportControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CreateProgressReportUseCase createUseCase;
    @Mock
    private ReviewProgressReportUseCase reviewUseCase;
    @Mock
    private QueryProgressReportUseCase queryUseCase;
    @Mock
    private AmendProgressReportUseCase amendUseCase;

    @InjectMocks
    private ProgressReportController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /api/progress-reports - Should create report and return 201 Created")
    void shouldCreateReport() throws Exception {
        CreateReportCommand command = new CreateReportCommand();
        command.setProjectId(10L);
        command.setReportType(ProgressReportType.PARTIAL);
        command.setPeriod("2026-I");
        command.setProgressPercentage(new BigDecimal("30.00"));
        command.setAchievements("achievements");
        command.setDifficulties("difficulties");
        command.setRecommendations("recommendations");

        ProgressReportResponse response = ProgressReportTestHelper.createResponse(1L, 10L, ProgressReportStatus.PENDING);

        when(createUseCase.create(any(CreateReportCommand.class))).thenReturn(response);

        mockMvc.perform(post("/api/progress-reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.projectId").value(10))
                .andExpect(jsonPath("$.reportType").value("PARTIAL"))
                .andExpect(jsonPath("$.reportStatus").value("PENDING"));

        verify(createUseCase, times(1)).create(any(CreateReportCommand.class));
    }

    @Test
    @DisplayName("GET /api/progress-reports/{id} - Should return 200 OK and report details")
    void shouldGetReportById() throws Exception {
        ProgressReportResponse response = ProgressReportTestHelper.createResponse(1L, 10L, ProgressReportStatus.PENDING);

        when(queryUseCase.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/progress-reports/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reportStatus").value("PENDING"));

        verify(queryUseCase, times(1)).getById(1L);
    }

    @Test
    @DisplayName("GET /api/progress-reports/project/{projectId} - Should return 200 OK and list of reports")
    void shouldListReportsByProject() throws Exception {
        ProgressReportResponse response = ProgressReportTestHelper.createResponse(1L, 10L, ProgressReportStatus.PENDING);

        when(queryUseCase.listByProject(10L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/progress-reports/project/{projectId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].projectId").value(10));

        verify(queryUseCase, times(1)).listByProject(10L);
    }

    @Test
    @DisplayName("PATCH /api/progress-reports/{id}/forward - Should forward report and return 200 OK")
    void shouldForwardReport() throws Exception {
        ProgressReportResponse response = ProgressReportTestHelper.createResponse(1L, null, ProgressReportStatus.UNDER_REVIEW);

        when(reviewUseCase.forwardToDirector(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/progress-reports/{id}/forward", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reportStatus").value("UNDER_REVIEW"));

        verify(reviewUseCase, times(1)).forwardToDirector(1L);
    }

    @Test
    @DisplayName("PATCH /api/progress-reports/{id}/approve - Should approve report and return 200 OK")
    void shouldApproveReport() throws Exception {
        ProgressReportResponse response = ProgressReportTestHelper.createResponse(1L, null, ProgressReportStatus.APPROVED);

        when(reviewUseCase.approve(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/progress-reports/{id}/approve", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reportStatus").value("APPROVED"));

        verify(reviewUseCase, times(1)).approve(1L);
    }

    @Test
    @DisplayName("PATCH /api/progress-reports/{id}/observe - Should observe report and return 200 OK")
    void shouldObserveReport() throws Exception {
        ProgressReportResponse response = ProgressReportTestHelper.createResponse(1L, null, ProgressReportStatus.OBSERVED);

        when(reviewUseCase.observe(1L, "Observation text")).thenReturn(response);

        mockMvc.perform(patch("/api/progress-reports/{id}/observe", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("observation", "Observation text"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reportStatus").value("OBSERVED"));

        verify(reviewUseCase, times(1)).observe(1L, "Observation text");
    }

    @Test
    @DisplayName("PATCH /api/progress-reports/{id}/reject - Should reject report and return 200 OK")
    void shouldRejectReport() throws Exception {
        ProgressReportResponse response = ProgressReportTestHelper.createResponse(1L, null, ProgressReportStatus.REJECTED);

        when(reviewUseCase.reject(1L)).thenReturn(response);

        mockMvc.perform(patch("/api/progress-reports/{id}/reject", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reportStatus").value("REJECTED"));

        verify(reviewUseCase, times(1)).reject(1L);
    }

    @Test
    @DisplayName("PATCH /api/progress-reports/{id}/amend - Should amend report and return 200 OK")
    void shouldAmendReport() throws Exception {
        AmendReportCommand command = new AmendReportCommand();
        command.setRequesterId(2L);
        command.setAmendmentDocumentId(99L);

        ProgressReportResponse response = ProgressReportTestHelper.createResponse(1L, null, ProgressReportStatus.UNDER_REVIEW);
        response.setAttachedDocumentId(99L);

        when(amendUseCase.amend(any(AmendReportCommand.class))).thenReturn(response);

        mockMvc.perform(patch("/api/progress-reports/{id}/amend", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.reportStatus").value("UNDER_REVIEW"))
                .andExpect(jsonPath("$.attachedDocumentId").value(99));

        verify(amendUseCase, times(1)).amend(any(AmendReportCommand.class));
    }
}
