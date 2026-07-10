package com.sgi.fiis.resolutions.application.usecase;

import com.sgi.fiis.resolutions.application.dto.ResolutionResponseDTO;
import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.out.ResolutionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetResolutionUseCase Unit Tests")
class GetResolutionUseCaseTest {

    @Mock
    private ResolutionRepositoryPort resolutionRepositoryPort;

    @InjectMocks
    private GetResolutionUseCase getResolutionUseCase;

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
    @DisplayName("Should return resolution DTO when found by id")
    void execute_ReturnsDto_WhenFound() {
        when(resolutionRepositoryPort.findById(1L)).thenReturn(Optional.of(sampleResolution));

        Optional<ResolutionResponseDTO> result = getResolutionUseCase.execute(1L);

        assertTrue(result.isPresent());
        ResolutionResponseDTO dto = result.get();
        assertEquals(1L, dto.idResolucion());
        assertEquals("RES-2023-001", dto.numeroResolucion());
        assertEquals(LocalDate.of(2023, Month.OCTOBER, 1), dto.fechaEmision());
        assertEquals("Thesis approval", dto.asunto());
        assertEquals(10L, dto.idTramite());
        assertEquals(100L, dto.idDocumentoAdjunto());

        verify(resolutionRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when resolution not found")
    void execute_ReturnsEmpty_WhenNotFound() {
        when(resolutionRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        Optional<ResolutionResponseDTO> result = getResolutionUseCase.execute(999L);

        assertTrue(result.isEmpty());
        verify(resolutionRepositoryPort).findById(999L);
    }
}
