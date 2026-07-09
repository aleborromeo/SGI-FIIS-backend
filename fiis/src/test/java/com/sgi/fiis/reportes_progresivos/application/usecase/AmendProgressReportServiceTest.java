package com.sgi.fiis.reportes_progresivos.application.usecase;

import com.sgi.fiis.reportes_progresivos.ProgressReportTestHelper;
import com.sgi.fiis.reportes_progresivos.application.dto.AmendReportCommand;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AmendProgressReportService.
 * Validates amend operation from OBSERVED and failure from other states.
 */
@ExtendWith(MockitoExtension.class)
class AmendProgressReportServiceTest {

    @Mock
    private ProgressReportRepositoryPort repositoryPort;

    private AmendProgressReportService service;

    @BeforeEach
    void setUp() {
        service = new AmendProgressReportService(repositoryPort);
    }

    private ProgressReport buildObservedReport() {
        return ProgressReportTestHelper.createReport(10L, 1L, ProgressReportStatus.OBSERVED);
    }

    private AmendReportCommand buildCommand(Long reportId) {
        return ProgressReportTestHelper.createAmendCommand(reportId);
    }

    private void mockFindAndSave(ProgressReport report) {
        when(repositoryPort.findById(10L)).thenReturn(Optional.of(report));
        when(repositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Amend transitions OBSERVED to UNDER_REVIEW with new document")
    void amendTransitionsToEnRevision() {
        ProgressReport report = buildObservedReport();
        mockFindAndSave(report);

        AmendReportCommand cmd = buildCommand(10L);
        ProgressReportResponse response = service.amend(cmd);

        assertEquals(ProgressReportStatus.UNDER_REVIEW, response.getReportStatus());
        assertEquals(55L, response.getAttachedDocumentId());
        verify(repositoryPort).save(any());
    }

    @Test
    @DisplayName("Amend without document still transitions to UNDER_REVIEW")
    void amendWithoutDocumentTransitions() {
        ProgressReport report = buildObservedReport();
        mockFindAndSave(report);

        AmendReportCommand cmd = buildCommand(10L);
        cmd.setAmendmentDocumentId(null);
        ProgressReportResponse response = service.amend(cmd);

        assertEquals(ProgressReportStatus.UNDER_REVIEW, response.getReportStatus());
    }

    @Test
    @DisplayName("Amend on report in UNDER_REVIEW throws IllegalStateException")
    void amendFromEnRevisionThrows() {
        ProgressReport report = ProgressReportTestHelper.createReport(10L, 1L, ProgressReportStatus.UNDER_REVIEW);

        when(repositoryPort.findById(10L)).thenReturn(Optional.of(report));

        AmendReportCommand cmd = buildCommand(10L);
        assertThrows(IllegalStateException.class, () -> service.amend(cmd));
        verify(repositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Amend with non-existent report throws ResourceNotFoundException")
    void amendNotFoundThrows() {
        when(repositoryPort.findById(999L)).thenReturn(Optional.empty());

        AmendReportCommand cmd = buildCommand(999L);
        assertThrows(ResourceNotFoundException.class, () -> service.amend(cmd));
        verify(repositoryPort, never()).save(any());
    }
}
