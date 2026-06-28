package com.sgi.fiis.reportes_progresivos.application.usecase;

import com.sgi.fiis.reportes_progresivos.ProgressReportTestHelper;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReviewProgressReportService.
 * Validates approve, observe, reject, and forward operations.
 */
@ExtendWith(MockitoExtension.class)
class ReviewProgressReportServiceTest {

    @Mock
    private ProgressReportRepositoryPort repositoryPort;

    private ReviewProgressReportService service;

    @BeforeEach
    void setUp() {
        service = new ReviewProgressReportService(repositoryPort);
    }

    private ProgressReport buildReportInReview() {
        return ProgressReportTestHelper.createReport(10L, 1L, ProgressReportStatus.UNDER_REVIEW);
    }

    private void mockFindAndSave(ProgressReport report) {
        when(repositoryPort.findById(10L)).thenReturn(Optional.of(report));
        when(repositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Approve transitions report to APPROVED")
    void approveTransitionsToAprobado() {
        ProgressReport report = buildReportInReview();
        mockFindAndSave(report);

        ProgressReportResponse response = service.approve(10L);

        assertEquals(ProgressReportStatus.APPROVED, response.getReportStatus());
        verify(repositoryPort).save(any());
    }

    @Test
    @DisplayName("Observe transitions report to OBSERVED")
    void observeTransitionsToObservado() {
        ProgressReport report = buildReportInReview();
        mockFindAndSave(report);

        ProgressReportResponse response = service.observe(10L, "Needs corrections");

        assertEquals(ProgressReportStatus.OBSERVED, response.getReportStatus());
    }

    @Test
    @DisplayName("Reject transitions report to REJECTED")
    void rejectTransitionsToRechazado() {
        ProgressReport report = buildReportInReview();
        mockFindAndSave(report);

        ProgressReportResponse response = service.reject(10L);

        assertEquals(ProgressReportStatus.REJECTED, response.getReportStatus());
    }

    @Test
    @DisplayName("Forward keeps UNDER_REVIEW status")
    void forwardKeepsEnRevision() {
        ProgressReport report = buildReportInReview();
        mockFindAndSave(report);

        ProgressReportResponse response = service.forwardToDirector(10L);

        assertEquals(ProgressReportStatus.UNDER_REVIEW, response.getReportStatus());
    }

    @Test
    @DisplayName("Approve with non-existent ID throws exception")
    void approveNotFoundThrows() {
        when(repositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.approve(999L));
        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Observe with non-existent ID throws exception")
    void observeNotFoundThrows() {
        when(repositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.observe(999L, "feedback"));
    }

    @Test
    @DisplayName("Reject with non-existent ID throws exception")
    void rejectNotFoundThrows() {
        when(repositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.reject(999L));
    }

    @Test
    @DisplayName("Forward with non-existent ID throws exception")
    void forwardNotFoundThrows() {
        when(repositoryPort.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.forwardToDirector(999L));
    }
}
