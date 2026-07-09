package com.sgi.fiis.reports.presentation.controller;

import com.sgi.fiis.reports.application.service.ReportService;
import com.sgi.fiis.reports.domain.model.*;
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
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for {@link ReportController}.
 * Uses standalone MockMvc to verify all REST endpoints (RF-94, RF-95).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportController - Web Layer Tests")
class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/projects
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/reports/projects → 200 OK with list of projects")
    void getProjectReport_noFilters_returns200() throws Exception {
        ProjectReport p = new ProjectReport();
        p.setProjectId(1);
        p.setProjectCode("PRY-2024-001");
        p.setProjectTitle("Sistema de Gestión");
        p.setProjectStatus("EN_EJECUCION");
        p.setBudget(BigDecimal.valueOf(15000));

        PaginatedResponse<ProjectReport> response =
                new PaginatedResponse<>(List.of(p), 1L, 0, 20);

        when(reportService.generateProjectReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/projects")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.data[0].projectCode").value("PRY-2024-001"))
                .andExpect(jsonPath("$.data[0].projectStatus").value("EN_EJECUCION"));

        verify(reportService, times(1)).generateProjectReport(any());
    }

    @Test
    @DisplayName("GET /api/reports/projects?status=APROBADO&page=1&size=5 → 200 OK paginated")
    void getProjectReport_withFiltersAndPagination_returns200() throws Exception {
        PaginatedResponse<ProjectReport> response =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 1, 5);

        when(reportService.generateProjectReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/projects")
                        .param("status", "APROBADO")
                        .param("page", "1")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.data").isArray());

        verify(reportService, times(1)).generateProjectReport(any());
    }

    @Test
    @DisplayName("GET /api/reports/projects → empty list returns 200 with data=[]")
    void getProjectReport_noResults_returns200EmptyList() throws Exception {
        PaginatedResponse<ProjectReport> response =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 0, 20);

        when(reportService.generateProjectReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/projects")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/procedures
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/reports/procedures → 200 OK with list of procedures")
    void getProcedureReport_noFilters_returns200() throws Exception {
        ProcedureReport t = new ProcedureReport();
        t.setProcedureId(5);
        t.setProcedureCode("TRM-2024-005");
        t.setProcedureType("PROYECTO");
        t.setCurrentStatus("EN_REVISION");

        PaginatedResponse<ProcedureReport> response =
                new PaginatedResponse<>(List.of(t), 1L, 0, 20);

        when(reportService.generateProcedureReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/procedures")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].procedureCode").value("TRM-2024-005"))
                .andExpect(jsonPath("$.data[0].procedureType").value("PROYECTO"));

        verify(reportService, times(1)).generateProcedureReport(any());
    }

    @Test
    @DisplayName("GET /api/reports/procedures?procedureType=PLAN_TESIS → 200 OK filtered")
    void getProcedureReport_withFilterType_returns200() throws Exception {
        PaginatedResponse<ProcedureReport> response =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 0, 20);

        when(reportService.generateProcedureReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/procedures")
                        .param("procedureType", "PLAN_TESIS")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(reportService, times(1)).generateProcedureReport(any());
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/resolutions
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/reports/resolutions → 200 OK with list of resolutions")
    void getResolutionReport_noFilters_returns200() throws Exception {
        ResolutionReport r = new ResolutionReport();
        r.setResolutionId(3);
        r.setResolutionNumber("RES-001-2024");
        r.setSubject("Aprobación de proyecto");
        r.setIssueDate(LocalDate.of(2024, Month.APRIL, 10));

        PaginatedResponse<ResolutionReport> response =
                new PaginatedResponse<>(List.of(r), 1L, 0, 20);

        when(reportService.generateResolutionReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/resolutions")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].resolutionNumber").value("RES-001-2024"));

        verify(reportService, times(1)).generateResolutionReport(any());
    }

    @Test
    @DisplayName("GET /api/reports/resolutions?fromDate=2024-01-01&toDate=2024-12-31 → 200 OK")
    void getResolutionReport_withFilterDates_returns200() throws Exception {
        PaginatedResponse<ResolutionReport> response =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 0, 20);

        when(reportService.generateResolutionReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/resolutions")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-12-31")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/progress-reports
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/reports/progress-reports → 200 OK with list of progress reports")
    void getProgressReport_noFilters_returns200() throws Exception {
        ProgressReport ia = new ProgressReport();
        ia.setReportId(2);
        ia.setProjectCode("PRY-2024-001");
        ia.setReportType("PARCIAL");
        ia.setReportStatus("APROBADO");
        ia.setProgressPercentage(BigDecimal.valueOf(45.5));

        PaginatedResponse<ProgressReport> response =
                new PaginatedResponse<>(List.of(ia), 1L, 0, 20);

        when(reportService.generateProgressReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/progress-reports")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].reportType").value("PARCIAL"))
                .andExpect(jsonPath("$.data[0].reportStatus").value("APROBADO"));

        verify(reportService, times(1)).generateProgressReport(any());
    }

    @Test
    @DisplayName("GET /api/reports/progress-reports?status=PENDIENTE → 200 OK filtered")
    void getProgressReport_withFilterStatus_returns200() throws Exception {
        PaginatedResponse<ProgressReport> response =
                new PaginatedResponse<>(Collections.emptyList(), 0L, 0, 20);

        when(reportService.generateProgressReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/progress-reports")
                        .param("status", "PENDIENTE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("All endpoints respond with Content-Type application/json")
    void allEndpoints_returnJsonContentType() throws Exception {
        PaginatedResponse<ProjectReport> response = new PaginatedResponse<>(List.of(), 0L, 0, 20);
        when(reportService.generateProjectReport(any())).thenReturn(response);

        mockMvc.perform(get("/api/reports/projects"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
