package com.sgi.fiis.convocatorias.application.usecases;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCallInteractor Unit Tests")
class CreateCallInteractorTest {

    @Mock
    private SaveCallPort saveCallPort;

    private CreateCallInteractor interactor;

    @BeforeEach
    void setUp() {
        interactor = new CreateCallInteractor(saveCallPort);
    }

    @Test
    @DisplayName("Should create a call successfully")
    void execute_shouldCreateCall() {
        var request = new CreateCallRequest(
                "Conv 2026", "Desc", LocalDate.now(), LocalDate.now().plusDays(30),
                1, "AMBOS", List.of(1, 2)
        );
        var savedCall = ResearchCall.builder()
                .id(1).title("Conv 2026").description("Desc")
                .startDate(request.getStartDate()).endDate(request.getEndDate())
                .status(CallStatus.OPEN).documentId(1).creatorId(10)
                .poblacionObjetivo("AMBOS").researchLineIds(List.of(1, 2))
                .build();

        when(saveCallPort.areLinesActive(List.of(1, 2))).thenReturn(true);
        when(saveCallPort.save(any(ResearchCall.class))).thenReturn(savedCall);

        CallResponse response = interactor.execute(request, 10);

        assertEquals("Conv 2026", response.getTitle());
        assertEquals("ABIERTA", response.getStatus());
        verify(saveCallPort).areLinesActive(List.of(1, 2));
        verify(saveCallPort).save(any(ResearchCall.class));
    }

    @Test
    @DisplayName("Should throw when lines are not active")
    void execute_shouldThrowWhenLinesNotActive() {
        var request = new CreateCallRequest(
                "Conv", "Desc", LocalDate.now(), LocalDate.now().plusDays(30),
                1, "AMBOS", List.of(99)
        );

        when(saveCallPort.areLinesActive(List.of(99))).thenReturn(false);

        assertThrows(com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException.class,
                () -> interactor.execute(request, 10));

        verify(saveCallPort, never()).save(any());
    }
}
