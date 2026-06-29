package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.ObservationRequestDTO;
import com.sgi.fiis.observations.application.dto.ObservationResponseDTO;
import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.model.ObservationStatus;
import com.sgi.fiis.observations.domain.model.ObservationType;
import com.sgi.fiis.observations.domain.port.ObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterObservationUseCaseTest {

    @Mock
    private ObservationRepository observationRepository;

    private RegisterObservationUseCase registerObservationUseCase;

    @BeforeEach
    void setUp() {
        registerObservationUseCase = new RegisterObservationUseCase(observationRepository);
    }

    @Test
    void execute_WhenRequestIsValid_ShouldSaveAndReturnResponse() {
        // Arrange
        ObservationRequestDTO request = ObservationRequestDTO.builder()
                .procedureId(1)
                .reviewerId(10)
                .type("TECNICA")
                .description("Falta la firma en el documento de propuesta tÃ©cnica")
                .reviewerRole("COORDINADOR_GRUPO")
                .build();

        LocalDateTime fechaFija = LocalDateTime.of(2026, Month.JUNE, 17, 12, 0);
        Observation observacionGuardada = Observation.builder()
                .id(100)
                .procedureId(1)
                .reviewerId(10)
                .type(ObservationType.TECNICA)
                .description("Falta la firma en el documento de propuesta tÃ©cnica")
                .status(ObservationStatus.PENDIENTE)
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(fechaFija)
                .updatedAt(fechaFija)
                .build();

        when(observationRepository.save(any(Observation.class))).thenReturn(observacionGuardada);

        // Act
        ObservationResponseDTO response = registerObservationUseCase.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals(100, response.getId());
        assertEquals(1, response.getProcedureId());
        assertEquals(10, response.getReviewerId());
        assertEquals("TECNICA", response.getType());
        assertEquals("Falta la firma en el documento de propuesta tÃ©cnica", response.getDescription());
        assertEquals("PENDIENTE", response.getStatus());
        assertEquals("COORDINADOR_GRUPO", response.getReviewerRole());
        assertEquals(fechaFija, response.getCreatedAt());
        assertEquals(fechaFija, response.getUpdatedAt());

        verify(observationRepository, times(1)).save(any(Observation.class));
    }

    @Test
    void execute_WhenInvalidTipoObservacion_ShouldThrowException() {
        // Arrange
        ObservationRequestDTO request = ObservationRequestDTO.builder()
                .procedureId(1)
                .reviewerId(10)
                .type("INVALIDA")
                .description("Falta la firma")
                .reviewerRole("COORDINADOR_GRUPO")
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> registerObservationUseCase.execute(request));
        verify(observationRepository, never()).save(any(Observation.class));
    }
}
