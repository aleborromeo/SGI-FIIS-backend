package com.sgi.fiis.observations.application.usecase;

import com.sgi.fiis.observations.application.dto.RemedyResponseDTO;
import com.sgi.fiis.observations.domain.model.Remedy;
import com.sgi.fiis.observations.domain.port.RemedyRepository;
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
@DisplayName("ListRemediesByObservationUseCase Unit Tests")
class ListRemediesByObservationUseCaseTest {

    @Mock
    private RemedyRepository remedyRepository;

    private ListRemediesByObservationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListRemediesByObservationUseCase(remedyRepository);
    }

    @Test
    @DisplayName("Should list remedies for a given observation")
    void shouldListRemediesByObservation() {
        Integer observationId = 100;
        LocalDateTime ahora = LocalDateTime.now();
        Remedy sub = Remedy.builder()
                .id(200)
                .observationId(observationId)
                .applicantId(5)
                .description("He subido el documento")
                .attachedDocumentId(45)
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        when(reremedyRepositoryExists(observationId)).thenReturn(Collections.singletonList(sub));

        List<RemedyResponseDTO> result = useCase.execute(observationId);

        assertNotNull(result);
        assertEquals(1, result.size());
        RemedyResponseDTO response = result.get(0);
        assertEquals(200, response.getId());
        assertEquals(observationId, response.getObservationId());
        assertEquals(5, response.getApplicantId());
        assertEquals("He subido el documento", response.getDescription());
        assertEquals(45, response.getAttachedDocumentId());

        verify(remedyRepository, times(1)).findByObservationId(observationId);
    }

    private List<Remedy> reremedyRepositoryExists(Integer observationId) {
        return remedyRepository.findByObservationId(observationId);
    }
}
