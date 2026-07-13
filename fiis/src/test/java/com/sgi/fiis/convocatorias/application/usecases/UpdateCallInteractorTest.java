package com.sgi.fiis.convocatorias.application.usecases;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.UpdateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UpdateCallInteractor Unit Tests")
class UpdateCallInteractorTest {

    private SaveCallPort saveCallPort;
    private UpdateCallInteractor interactor;

    private static final LocalDate START = LocalDate.of(2026, Month.JANUARY, 1);
    private static final LocalDate END = LocalDate.of(2026, Month.DECEMBER, 31);

    @BeforeEach
    void setup() {
        saveCallPort = mock(SaveCallPort.class);
        interactor = new UpdateCallInteractor(saveCallPort);
    }

    private ResearchCall openCall() {
        return new ResearchCall(1, "Original Title", "Original Desc", START, END,
                CallStatus.OPEN, null, 10, List.of(1, 2));
    }

    private ResearchCall closedCall() {
        return new ResearchCall(1, "Closed Call", "Desc", START, END,
                CallStatus.CLOSED, null, null);
    }

    private ResearchCall finishedCall() {
        return new ResearchCall(1, "Finished Call", "Desc", START, END,
                CallStatus.FINISHED, null, null);
    }

    @Test
    @DisplayName("execute: throws when call not found")
    void execute_notFound() {
        when(saveCallPort.findById(99)).thenReturn(Optional.empty());
        UpdateCallRequest request = new UpdateCallRequest();

        assertThrows(BusinessRuleValidationException.class,
                () -> interactor.execute(99, request));
    }

    @Test
    @DisplayName("execute: throws when call is not OPEN")
    void execute_notOpen() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(closedCall()));
        UpdateCallRequest request = new UpdateCallRequest();

        assertThrows(BusinessRuleValidationException.class,
                () -> interactor.execute(1, request));
    }

    @Test
    @DisplayName("execute: throws when call is FINISHED")
    void execute_finished() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(finishedCall()));
        UpdateCallRequest request = new UpdateCallRequest();

        assertThrows(BusinessRuleValidationException.class,
                () -> interactor.execute(1, request));
    }

    @Test
    @DisplayName("execute: updates with all fields provided")
    void execute_allFields() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));
        when(saveCallPort.areLinesActive(any())).thenReturn(true);

        ResearchCall saved = new ResearchCall(1, "New Title", "New Desc",
                LocalDate.of(2026, Month.MARCH, 1), LocalDate.of(2026, Month.SEPTEMBER, 30),
                CallStatus.OPEN, 5, 10, List.of(3));
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = UpdateCallRequest.builder()
                .title("New Title")
                .description("New Desc")
                .startDate(LocalDate.of(2026, Month.MARCH, 1))
                .endDate(LocalDate.of(2026, Month.SEPTEMBER, 30))
                .documentId(5)
                .researchLineIds(List.of(3))
                .build();

        CallResponse response = interactor.execute(1, request);

        assertNotNull(response);
        assertEquals("New Title", response.getTitle());
        verify(saveCallPort).save(any());
    }

    @Test
    @DisplayName("execute: keeps original values when request fields are null")
    void execute_nullFieldsKeepOriginal() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));

        ResearchCall saved = openCall();
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = new UpdateCallRequest();

        CallResponse response = interactor.execute(1, request);

        assertEquals("Original Title", response.getTitle());
        assertEquals("Original Desc", response.getDescription());
    }

    @Test
    @DisplayName("execute: blank title keeps original")
    void execute_blankTitleKeepsOriginal() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));

        ResearchCall saved = openCall();
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = UpdateCallRequest.builder().title("   ").build();

        CallResponse response = interactor.execute(1, request);

        assertEquals("Original Title", response.getTitle());
    }

    @Test
    @DisplayName("execute: validates active lines")
    void execute_linesNotActive() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));
        when(saveCallPort.areLinesActive(any())).thenReturn(false);

        UpdateCallRequest request = UpdateCallRequest.builder()
                .researchLineIds(List.of(99))
                .build();

        assertThrows(BusinessRuleValidationException.class,
                () -> interactor.execute(1, request));
    }

    @Test
    @DisplayName("execute: skips line validation when researchLineIds is null")
    void execute_nullLineIdsSkipsValidation() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));

        ResearchCall saved = openCall();
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = UpdateCallRequest.builder().title("Updated").build();

        assertDoesNotThrow(() -> interactor.execute(1, request));
        verify(saveCallPort, never()).areLinesActive(any());
    }

    @Test
    @DisplayName("execute: skips line validation when researchLineIds is empty")
    void execute_emptyLineIdsSkipsValidation() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));

        ResearchCall saved = openCall();
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = UpdateCallRequest.builder()
                .researchLineIds(List.of())
                .build();

        assertDoesNotThrow(() -> interactor.execute(1, request));
        verify(saveCallPort, never()).areLinesActive(any());
    }

    @Test
    @DisplayName("execute: updates researchLineIds from request")
    void execute_updatesResearchLineIds() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));
        when(saveCallPort.areLinesActive(List.of(5))).thenReturn(true);

        ResearchCall saved = new ResearchCall(1, "Original Title", "Original Desc", START, END,
                CallStatus.OPEN, null, 10, List.of(5));
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = UpdateCallRequest.builder()
                .researchLineIds(List.of(5))
                .build();

        interactor.execute(1, request);

        verify(saveCallPort).save(argThat(c -> c.getResearchLineIds().equals(List.of(5))));
    }

    @Test
    @DisplayName("execute: keeps original researchLineIds when request is null")
    void execute_keepsOriginalLineIds() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));

        ResearchCall saved = openCall();
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = new UpdateCallRequest();

        interactor.execute(1, request);

        verify(saveCallPort).save(argThat(c -> c.getResearchLineIds().equals(List.of(1, 2))));
    }

    @Test
    @DisplayName("mapToResponse: maps CLOSED status")
    void mapToResponse_closed() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));
        when(saveCallPort.save(any())).thenReturn(closedCall());

        CallResponse response = interactor.execute(1, new UpdateCallRequest());

        assertEquals("CERRADA", response.getStatus());
    }

    @Test
    @DisplayName("mapToResponse: maps FINISHED status")
    void mapToResponse_finished() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));
        when(saveCallPort.save(any())).thenReturn(finishedCall());

        CallResponse response = interactor.execute(1, new UpdateCallRequest());

        assertEquals("FINALIZADA", response.getStatus());
    }

    @Test
    @DisplayName("mapToResponse: maps OPEN status to ABIERTA")
    void mapToResponse_open() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));
        when(saveCallPort.save(any())).thenReturn(openCall());

        CallResponse response = interactor.execute(1, new UpdateCallRequest());

        assertEquals("ABIERTA", response.getStatus());
    }

    @Test
    @DisplayName("execute: resolves startDate from request when provided")
    void execute_resolvesStartDate() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));

        LocalDate newStart = LocalDate.of(2026, Month.MARCH, 1);
        ResearchCall saved = new ResearchCall(1, "Original Title", "Original Desc",
                newStart, END, CallStatus.OPEN, null, 10, List.of(1, 2));
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = UpdateCallRequest.builder().startDate(newStart).build();

        CallResponse response = interactor.execute(1, request);

        assertEquals(newStart, response.getStartDate());
    }

    @Test
    @DisplayName("execute: resolves endDate from request when provided")
    void execute_resolvesEndDate() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));

        LocalDate newEnd = LocalDate.of(2026, Month.JUNE, 30);
        ResearchCall saved = new ResearchCall(1, "Original Title", "Original Desc",
                START, newEnd, CallStatus.OPEN, null, 10, List.of(1, 2));
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = UpdateCallRequest.builder().endDate(newEnd).build();

        CallResponse response = interactor.execute(1, request);

        assertEquals(newEnd, response.getEndDate());
    }

    @Test
    @DisplayName("execute: resolves documentId from request when provided")
    void execute_resolvesDocumentId() {
        when(saveCallPort.findById(1)).thenReturn(Optional.of(openCall()));

        ResearchCall saved = new ResearchCall(1, "Original Title", "Original Desc",
                START, END, CallStatus.OPEN, 42, 10, List.of(1, 2));
        when(saveCallPort.save(any())).thenReturn(saved);

        UpdateCallRequest request = UpdateCallRequest.builder().documentId(42).build();

        CallResponse response = interactor.execute(1, request);

        assertEquals(42, response.getDocumentId());
    }

    @Test
    @DisplayName("execute: keeps original documentId when request is null")
    void execute_keepsOriginalDocumentId() {
        ResearchCall withDoc = new ResearchCall(1, "Title", "Desc", START, END,
                CallStatus.OPEN, 7, 10, List.of(1));
        when(saveCallPort.findById(1)).thenReturn(Optional.of(withDoc));
        when(saveCallPort.save(any())).thenReturn(withDoc);

        CallResponse response = interactor.execute(1, new UpdateCallRequest());

        assertEquals(7, response.getDocumentId());
    }
}
