package com.sgi.fiis.reports.application.service;

import com.sgi.fiis.reports.domain.model.*;
import com.sgi.fiis.reports.domain.repository.ReportRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReportService}.
 * Verifies pagination and delegation to repository for RF-94 and RF-95.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService - Unit Tests")
class ReportServiceTest {

    @Mock
    private ReportRepositoryPort repo;

    @InjectMocks
    private ReportService service;

    private ReportFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ReportFilter();
        filter.setPage(0);
        filter.setSize(10);
    }

    // -------------------------------------------------------------------------
    // RF-94: Project Report
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("RF-94: Should return PaginatedResponse with projects and correct total")
    void generateProjectReport_withData_returnsPaginatedCorrectly() {
        // arrange
        ProjectReport project = new ProjectReport();
        when(repo.findProjects(any())).thenReturn(List.of(project));
        when(repo.countProjects(any())).thenReturn(1L);

        // act
        PaginatedResponse<ProjectReport> response = service.generateProjectReport(filter);

        // assert
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(1L);
        assertThat(response.getPage()).isZero();
        assertThat(response.getSize()).isEqualTo(10);
        verify(repo, times(1)).findProjects(filter);
        verify(repo, times(1)).countProjects(filter);
    }

    @Test
    @DisplayName("RF-94: Should return empty list when no projects exist")
    void generateProjectReport_noData_returnsEmptyList() {
        // arrange
        when(repo.findProjects(any())).thenReturn(Collections.emptyList());
        when(repo.countProjects(any())).thenReturn(0L);

        // act
        PaginatedResponse<ProjectReport> response = service.generateProjectReport(filter);

        // assert
        assertThat(response.getData()).isEmpty();
        assertThat(response.getTotal()).isZero();
    }

    // -------------------------------------------------------------------------
    // RF-95: Procedure Report
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("RF-95: Should return PaginatedResponse with procedures and correct total")
    void generateProcedureReport_withData_returnsPaginatedCorrectly() {
        // arrange
        ProcedureReport procedure = new ProcedureReport();
        when(repo.findProcedures(any())).thenReturn(List.of(procedure));
        when(repo.countProcedures(any())).thenReturn(5L);

        // act
        PaginatedResponse<ProcedureReport> response = service.generateProcedureReport(filter);

        // assert
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(5L);
        verify(repo, times(1)).findProcedures(filter);
        verify(repo, times(1)).countProcedures(filter);
    }

    @Test
    @DisplayName("RF-95: Should return empty list when no procedures exist")
    void generateProcedureReport_noData_returnsEmptyList() {
        // arrange
        when(repo.findProcedures(any())).thenReturn(Collections.emptyList());
        when(repo.countProcedures(any())).thenReturn(0L);

        // act
        PaginatedResponse<ProcedureReport> response = service.generateProcedureReport(filter);

        // assert
        assertThat(response.getData()).isEmpty();
        assertThat(response.getTotal()).isZero();
    }

    @Test
    @DisplayName("Should return empty list when no resolutions exist")
    void generateResolutionReport_noData_returnsEmptyList() {
        // arrange
        when(repo.findResolutions(any())).thenReturn(Collections.emptyList());
        when(repo.countResolutions(any())).thenReturn(0L);

        // act
        PaginatedResponse<ResolutionReport> response = service.generateResolutionReport(filter);

        // assert
        assertThat(response.getData()).isEmpty();
        assertThat(response.getTotal()).isZero();
    }

    @Test
    @DisplayName("Should return empty list when no progress reports exist")
    void generateProgressReport_noData_returnsEmptyList() {
        // arrange
        when(repo.findProgressReports(any())).thenReturn(Collections.emptyList());
        when(repo.countProgressReports(any())).thenReturn(0L);

        // act
        PaginatedResponse<ProgressReport> response = service.generateProgressReport(filter);

        // assert
        assertThat(response.getData()).isEmpty();
        assertThat(response.getTotal()).isZero();
    }

    // -------------------------------------------------------------------------
    // Resolution Report
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return PaginatedResponse with resolutions correctly")
    void generateResolutionReport_withData_returnsPaginatedCorrectly() {
        // arrange
        ResolutionReport resolution = new ResolutionReport();
        when(repo.findResolutions(any())).thenReturn(List.of(resolution));
        when(repo.countResolutions(any())).thenReturn(3L);

        // act
        PaginatedResponse<ResolutionReport> response = service.generateResolutionReport(filter);

        // assert
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(3L);
        verify(repo, times(1)).findResolutions(filter);
        verify(repo, times(1)).countResolutions(filter);
    }

    // -------------------------------------------------------------------------
    // Progress Report
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return PaginatedResponse with progress reports correctly")
    void generateProgressReport_withData_returnsPaginatedCorrectly() {
        // arrange
        ProgressReport report = new ProgressReport();
        when(repo.findProgressReports(any())).thenReturn(List.of(report));
        when(repo.countProgressReports(any())).thenReturn(2L);

        // act
        PaginatedResponse<ProgressReport> response = service.generateProgressReport(filter);

        // assert
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getTotal()).isEqualTo(2L);
        verify(repo, times(1)).findProgressReports(filter);
        verify(repo, times(1)).countProgressReports(filter);
    }

    // -------------------------------------------------------------------------
    // Pagination verification: page and size propagation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should propagate page and size from filter to paginated response")
    void generateProjectReport_propagatesPageAndSize() {
        // arrange
        filter.setPage(2);
        filter.setSize(5);
        when(repo.findProjects(any())).thenReturn(Collections.emptyList());
        when(repo.countProjects(any())).thenReturn(50L);

        // act
        PaginatedResponse<ProjectReport> response = service.generateProjectReport(filter);

        // assert
        assertThat(response.getPage()).isEqualTo(2);
        assertThat(response.getSize()).isEqualTo(5);
        assertThat(response.getTotal()).isEqualTo(50L);
    }
}
