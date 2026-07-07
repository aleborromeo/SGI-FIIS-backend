package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.ObservationResponseDTO;
import com.sgi.fiis.observations.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.model.ObservationStatus;
import com.sgi.fiis.observations.domain.model.ObservationType;
import com.sgi.fiis.observations.domain.port.ObservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetObservationUseCase Unit Tests")
class GetObservationUseCaseTest {

    @Mock
    private ObservationRepository observationRepository;

    private GetObservationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetObservationUseCase(observationRepository);
    }

    @Test
    @DisplayName("Should return observation details when observation exists")
    void shouldReturnDetailsWhenObservationExists() {
        Integer observationId = 100;
        LocalDateTime ahora = LocalDateTime.now();
        Observation obs = Observation.builder()
                .id(observationId)
                .procedureId(1)
                .reviewerId(10)
                .type(ObservationType.TECNICA)
                .description("Falta firma")
                .status(ObservationStatus.PENDIENTE)
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        when(observationRepository.findById(observationId)).thenReturn(Optional.of(obs));

        ObservationResponseDTO response = useCase.execute(observationId);

        assertNotNull(response);
        assertEquals(100, response.getId());
        assertEquals(1, response.getProcedureId());
        assertEquals("TECNICA", response.getType());
        assertEquals("PENDIENTE", response.getStatus());

        verify(observationRepository, times(1)).findById(observationId);
    }

    @Test
    @DisplayName("Should throw exception when observation does not exist")
    void shouldThrowExceptionWhenObservationDoesNotExist() {
        Integer observationId = 999;
        when(observationRepository.findById(observationId)).thenReturn(Optional.empty());

        assertThrows(ObservationNotFoundException.class, () -> useCase.execute(observationId));

        verify(observationRepository, times(1)).findById(observationId);
    }
}
