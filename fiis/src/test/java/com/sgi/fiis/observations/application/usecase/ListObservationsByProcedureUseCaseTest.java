package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.ObservationResponseDTO;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListObservationsByProcedureUseCase Unit Tests")
class ListObservationsByProcedureUseCaseTest {

    @Mock
    private ObservationRepository observationRepository;

    private ListObservationsByProcedureUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListObservationsByProcedureUseCase(observationRepository);
    }

    @Test
    @DisplayName("Should list observations for a given procedure")
    void shouldListObservationsByProcedure() {
        Integer procedureId = 1;
        LocalDateTime ahora = LocalDateTime.now();
        Observation obs = Observation.builder()
                .id(100)
                .procedureId(procedureId)
                .reviewerId(10)
                .type(ObservationType.TECHNICAL)
                .description("Falta firma")
                .status(ObservationStatus.PENDING)
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        when(observationRepository.findByProcedureId(procedureId)).thenReturn(Collections.singletonList(obs));

        List<ObservationResponseDTO> result = useCase.execute(procedureId);

        assertNotNull(result);
        assertEquals(1, result.size());
        ObservationResponseDTO response = result.get(0);
        assertEquals(100, response.getId());
        assertEquals(1, response.getProcedureId());
        assertEquals("TECHNICAL", response.getType());
        assertEquals("PENDING", response.getStatus());
        assertEquals("COORDINADOR_GRUPO", response.getReviewerRole());

        verify(observationRepository, times(1)).findByProcedureId(procedureId);
    }
}
