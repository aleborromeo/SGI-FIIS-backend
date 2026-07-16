package com.sgi.fiis.reportes_progresivos.application.usecase;

import com.sgi.fiis.reportes_progresivos.ProgressReportTestHelper;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import com.sgi.fiis.shared.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for QueryProgressReportService.
 * Validates listing by project and get-by-id operations.
 */
@ExtendWith(MockitoExtension.class)
class QueryProgressReportServiceTest {

    @Mock
    private ProgressReportRepositoryPort repositoryPort;

    private QueryProgressReportService service;

    @BeforeEach
    void setUp() {
        service = new QueryProgressReportService(repositoryPort);
    }

    private ProgressReport buildReport(Long id, Long projectId) {
        return ProgressReportTestHelper.createReport(id, projectId, ProgressReportStatus.PENDING);
    }

    @Test
    @DisplayName("List by project returns mapped responses")
    void listByProjectReturnsMappedResponses() {
        ProgressReport r1 = buildReport(1L, 5L);
        ProgressReport r2 = buildReport(2L, 5L);
        when(repositoryPort.findByProjectId(5L)).thenReturn(Arrays.asList(r1, r2));

        List<ProgressReportResponse> result = service.listByProject(5L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    @DisplayName("List by project returns empty list when no reports exist")
    void listByProjectReturnsEmptyList() {
        when(repositoryPort.findByProjectId(99L)).thenReturn(Collections.emptyList());

        List<ProgressReportResponse> result = service.listByProject(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Get by ID returns mapped response")
    void getByIdReturnsResponse() {
        ProgressReport report = buildReport(10L, 5L);
        when(repositoryPort.findById(10L)).thenReturn(Optional.of(report));

        ProgressReportResponse response = service.getById(10L);

        assertEquals(10L, response.getId());
        assertEquals(5L, response.getProjectId());
    }

    @Test
    @DisplayName("Get by ID with non-existent ID throws exception")
    void getByIdNotFoundThrows() {
        when(repositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(999L));
    }

    @Test
    @DisplayName("List all returns mapped responses")
    void listAllReturnsMappedResponses() {
        ProgressReport r1 = buildReport(1L, 5L);
        ProgressReport r2 = buildReport(2L, 6L);
        when(repositoryPort.findAll()).thenReturn(Arrays.asList(r1, r2));

        List<ProgressReportResponse> result = service.listAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    @DisplayName("List all returns empty list")
    void listAllReturnsEmptyList() {
        when(repositoryPort.findAll()).thenReturn(Collections.emptyList());

        List<ProgressReportResponse> result = service.listAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("List by status returns mapped responses")
    void listByStatusReturnsMappedResponses() {
        ProgressReport r1 = buildReport(1L, 5L);
        when(repositoryPort.findByStatus("PENDIENTE")).thenReturn(Arrays.asList(r1));

        List<ProgressReportResponse> result = service.listByStatus("PENDIENTE");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("List by status returns empty list")
    void listByStatusReturnsEmptyList() {
        when(repositoryPort.findByStatus("RECHAZADO")).thenReturn(Collections.emptyList());

        List<ProgressReportResponse> result = service.listByStatus("RECHAZADO");

        assertTrue(result.isEmpty());
    }
}
