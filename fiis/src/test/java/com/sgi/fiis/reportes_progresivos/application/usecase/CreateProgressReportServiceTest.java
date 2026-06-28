package com.sgi.fiis.reportes_progresivos.application.usecase;

import com.sgi.fiis.reportes_progresivos.ProgressReportTestHelper;
import com.sgi.fiis.reportes_progresivos.application.dto.CreateReportCommand;
import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportRepositoryPort;
import com.sgi.fiis.reportes_progresivos.domain.port.out.ProgressReportEventPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateProgressReportService.
 * Mocks repository and event ports -- no Spring context needed.
 */
@ExtendWith(MockitoExtension.class)
class CreateProgressReportServiceTest {

    @Mock
    private ProgressReportRepositoryPort repositoryPort;

    @Mock
    private ProgressReportEventPort procedureEventPort;

    private CreateProgressReportService service;

    @BeforeEach
    void setUp() {
        service = new CreateProgressReportService(repositoryPort, procedureEventPort);
    }

    private CreateReportCommand buildCommand() {
        CreateReportCommand cmd = ProgressReportTestHelper.createCommand();
        cmd.setProjectId(1L);
        cmd.setRequesterId(10L);
        cmd.setGroupId(5L);
        cmd.setProgressPercentage(new BigDecimal("45.00"));
        cmd.setAchievements("Milestone reached");
        cmd.setDifficulties("None");
        cmd.setRecommendations("Continue");
        return cmd;
    }

    private void mockSaveWithId(Long id) {
        when(repositoryPort.save(any(ProgressReport.class))).thenAnswer(invocation -> {
            ProgressReport arg = invocation.getArgument(0);
            arg.setId(id);
            return arg;
        });
    }

    @Test
    @DisplayName("Create persists report in UNDER_REVIEW status")
    void createPersistsReportInReview() {
        CreateReportCommand cmd = buildCommand();
        ArgumentCaptor<ProgressReport> captor = ArgumentCaptor.forClass(ProgressReport.class);

        mockSaveWithId(100L);

        ProgressReportResponse response = service.create(cmd);

        verify(repositoryPort).save(captor.capture());
        ProgressReport saved = captor.getValue();
        assertEquals(ProgressReportStatus.UNDER_REVIEW, saved.getReportStatus());
        assertEquals(1L, saved.getProjectId());
        assertNotNull(response);
        assertEquals(100L, response.getId());
    }

    @Test
    @DisplayName("Create attaches document when provided")
    void createAttachesDocumentWhenProvided() {
        CreateReportCommand cmd = buildCommand();
        cmd.setAttachedDocumentId(77L);

        mockSaveWithId(101L);

        service.create(cmd);

        ArgumentCaptor<ProgressReport> captor = ArgumentCaptor.forClass(ProgressReport.class);
        verify(repositoryPort).save(captor.capture());
        assertEquals(77L, captor.getValue().getAttachedDocumentId());
    }

    @Test
    @DisplayName("Create does not attach document when null")
    void createDoesNotAttachDocumentWhenNull() {
        CreateReportCommand cmd = buildCommand();
        cmd.setAttachedDocumentId(null);

        mockSaveWithId(102L);

        service.create(cmd);

        ArgumentCaptor<ProgressReport> captor = ArgumentCaptor.forClass(ProgressReport.class);
        verify(repositoryPort).save(captor.capture());
        assertNull(captor.getValue().getAttachedDocumentId());
    }

    @Test
    @DisplayName("Create publishes procedure event after persisting")
    void createPublishesProcedureEvent() {
        CreateReportCommand cmd = buildCommand();

        mockSaveWithId(103L);

        service.create(cmd);

        verify(procedureEventPort).publishReportProcedure(103L, 1L, 10L, 5L);
    }

    @Test
    @DisplayName("Create with invalid percentage throws exception")
    void createWithInvalidPercentageThrows() {
        CreateReportCommand cmd = buildCommand();
        cmd.setProgressPercentage(new BigDecimal("150"));

        assertThrows(IllegalArgumentException.class, () -> service.create(cmd));
        verify(repositoryPort, never()).save(any());
    }
}
