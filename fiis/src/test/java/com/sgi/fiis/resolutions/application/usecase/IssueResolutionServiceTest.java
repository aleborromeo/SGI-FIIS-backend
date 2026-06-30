package com.sgi.fiis.resolutions.application.usecase;

import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.in.IssueResolutionCommand;
import com.sgi.fiis.resolutions.domain.port.out.DocumentStoragePort;
import com.sgi.fiis.resolutions.domain.port.out.ResolutionRepositoryPort;
import com.sgi.fiis.resolutions.domain.port.out.ProcedureRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueResolutionServiceTest {

    @Mock
    private ResolutionRepositoryPort resolutionRepositoryPort;

    @Mock
    private ProcedureRepositoryPort procedureRepositoryPort;

    @Mock
    private DocumentStoragePort documentStoragePort;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private IssueResolutionService issueResolutionService;

    private IssueResolutionCommand command;

    @BeforeEach
    void setUp() {
        command = new IssueResolutionCommand(
                "RES-2023-001",
                LocalDate.now(),
                "Thesis approval",
                1L,
                new byte[]{1, 2, 3},
                "resolution.pdf",
                "application/pdf"
        );
    }

    @Test
    void issue_whenEverythingIsValid_shouldSaveAndReturnResolution() {
        // Arrange
        when(procedureRepositoryPort.existsProcedure(1L)).thenReturn(true);
        when(resolutionRepositoryPort.existsByNumber("RES-2023-001")).thenReturn(false);
        when(documentStoragePort.saveDocument(any(), any(), any())).thenReturn(100L);

        Resolution sampleResolution = new Resolution(
                1L, "RES-2023-001", LocalDate.now(), "Thesis approval", 1L, 100L, LocalDateTime.now()
        );
        when(resolutionRepositoryPort.save(any(Resolution.class))).thenReturn(sampleResolution);

        // Act
        Resolution result = issueResolutionService.issue(command);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.idResolucion());
        assertEquals("RES-2023-001", result.numeroResolucion());

        verify(procedureRepositoryPort).existsProcedure(1L);
        verify(resolutionRepositoryPort).existsByNumber("RES-2023-001");
        verify(documentStoragePort).saveDocument(any(), any(), any());
        verify(resolutionRepositoryPort).save(any(Resolution.class));
        verify(procedureRepositoryPort).updateStatusToApprovedWithResolution(1L);
    }

    @Test
    void issue_whenProcedureDoesNotExist_shouldThrowException() {
        // Arrange
        when(procedureRepositoryPort.existsProcedure(1L)).thenReturn(false);
        when(messageSource.getMessage(eq("resolution.error.procedure-not-found"), any(), any(Locale.class)))
                .thenReturn("The specified procedure does not exist.");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            issueResolutionService.issue(command);
        });

        assertEquals("The specified procedure does not exist.", exception.getMessage());

        // Verify that no further methods are called
        verify(resolutionRepositoryPort, never()).existsByNumber(any());
        verify(documentStoragePort, never()).saveDocument(any(), any(), any());
        verify(resolutionRepositoryPort, never()).save(any());
    }

    @Test
    void issue_whenResolutionAlreadyExists_shouldThrowException() {
        // Arrange
        when(procedureRepositoryPort.existsProcedure(1L)).thenReturn(true);
        when(resolutionRepositoryPort.existsByNumber("RES-2023-001")).thenReturn(true);
        when(messageSource.getMessage(eq("resolution.error.duplicate-number"), any(), any(Locale.class)))
                .thenReturn("The resolution number is already registered.");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            issueResolutionService.issue(command);
        });

        assertEquals("The resolution number is already registered.", exception.getMessage());

        // Verify that no documents or resolutions are saved
        verify(documentStoragePort, never()).saveDocument(any(), any(), any());
        verify(resolutionRepositoryPort, never()).save(any());
    }
}
