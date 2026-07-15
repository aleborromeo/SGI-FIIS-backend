package com.sgi.fiis.convocatorias;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.out.SaveCallPort;
import com.sgi.fiis.convocatorias.application.usecases.CallInteractor;
import com.sgi.fiis.convocatorias.application.usecases.CreateCallInteractor;
import com.sgi.fiis.convocatorias.domain.model.CallStatus;
import com.sgi.fiis.convocatorias.domain.model.ResearchCall;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SuppressWarnings("all")
class ResearchCallModuleTest {

    // Fixed dates to avoid system clock usage in tests (SonarCloud S5977)
    private static final LocalDate FIXED_TODAY = LocalDate.of(2026, Month.JUNE, 1);
    private static final LocalDate FIXED_PAST_5 = LocalDate.of(2026, Month.MAY, 27);
    private static final LocalDate FIXED_PAST_10 = LocalDate.of(2026, Month.MAY, 22);
    private static final LocalDate FIXED_FUTURE_2M = LocalDate.of(2026, Month.AUGUST, 1);
    private static final LocalDate FIXED_FUTURE_5D = LocalDate.of(2026, Month.JUNE, 6);
    private static final LocalDate FIXED_PAST_2D = LocalDate.of(2026, Month.MAY, 30);
    private static final LocalDate FIXED_FUTURE_10D = LocalDate.of(2026, Month.JUNE, 11);

    private SaveCallPort saveCallPort;
    private CreateCallInteractor createCallInteractor;
    private CallInteractor callInteractor;

    @BeforeEach
    @SuppressWarnings("unused")
    void setup() {
        saveCallPort = mock(SaveCallPort.class);
        createCallInteractor = new CreateCallInteractor(saveCallPort);
        callInteractor = new CallInteractor(saveCallPort);
    }

    @Test
    void shouldCreateCallSuccessfully() {
        CreateCallRequest request = new CreateCallRequest();
        request.setTitle("Call 2026");
        request.setDescription("Research call description");
        request.setStartDate(FIXED_TODAY);
        request.setEndDate(FIXED_FUTURE_2M);
        request.setResearchLineIds(Collections.singletonList(1));

        when(saveCallPort.areLinesActive(any())).thenReturn(true);

        ResearchCall savedCall = new ResearchCall(1, "Call 2026", "Research call description", FIXED_TODAY,
                FIXED_FUTURE_2M, CallStatus.OPEN, null, 1, Collections.singletonList(1));
        when(saveCallPort.save(any(ResearchCall.class))).thenReturn(savedCall);

        CallResponse response = createCallInteractor.execute(request, 1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Call 2026", response.getTitle());
        assertEquals("ABIERTA", response.getStatus());

        verify(saveCallPort).save(argThat(call ->
                call.getCreatorId() != null && call.getCreatorId() == 1
        ));
    }

    @Test
    void shouldValidateDatesCorrectlyOnSubmission() {
        // Active open call - single invocation in lambda (SonarCloud S5778)
        ResearchCall openCall = new ResearchCall(1, "Call 1", "Description", FIXED_PAST_5, FIXED_FUTURE_5D,
                CallStatus.OPEN, null, null);
        assertDoesNotThrow(() -> openCall.validateCanSubmitProject(FIXED_TODAY));

        // Closed call - single invocation in lambda
        ResearchCall closedCall = new ResearchCall(2, "Call 2", "Description", FIXED_PAST_5, FIXED_FUTURE_5D,
                CallStatus.CLOSED, null, null);
        var ex1 = assertThrows(BusinessRuleValidationException.class,
                () -> closedCall.validateCanSubmitProject(FIXED_TODAY));
        assertNotNull(ex1);


        // Expired call - single invocation in lambda
        ResearchCall expiredCall = new ResearchCall(3, "Call 3", "Description", FIXED_PAST_10, FIXED_PAST_2D,
                CallStatus.OPEN, null, null);
        var ex2 = assertThrows(BusinessRuleValidationException.class,
                () -> expiredCall.validateCanSubmitProject(FIXED_TODAY));
        assertNotNull(ex2);

    }

    @Test
    void shouldListCallsFilteredByStatus() {
        ResearchCall call1 = new ResearchCall(1, "Call 1", "Description", FIXED_TODAY, FIXED_FUTURE_10D,
                CallStatus.OPEN, null, null);
        ResearchCall call2 = new ResearchCall(2, "Call 2", "Description", FIXED_TODAY, FIXED_FUTURE_10D,
                CallStatus.CLOSED, null, null);

        when(saveCallPort.findByStatus(CallStatus.OPEN)).thenReturn(Arrays.asList(call1));
        when(saveCallPort.findAll()).thenReturn(Arrays.asList(call1, call2));

        List<CallResponse> openCalls = callInteractor.getCalls("ABIERTA");
        assertEquals(1, openCalls.size());
        assertEquals("Call 1", openCalls.get(0).getTitle());

        List<CallResponse> allCalls = callInteractor.getCalls(null);
        assertEquals(2, allCalls.size());
    }

    @Test
    void shouldFindCallsWithEmptyStatus() {
        ResearchCall call = new ResearchCall(1, "Call 1", "Description", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.OPEN,
                null, null);
        when(saveCallPort.findAll()).thenReturn(Arrays.asList(call));

        List<CallResponse> allCalls = callInteractor.execute("   ");
        assertEquals(1, allCalls.size());
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        var ex = assertThrows(BusinessRuleValidationException.class, () -> callInteractor.getCalls("INVALID_STATUS"));
        assertNotNull(ex);
    }

    @Test
    void shouldGetCallByIdSuccessfully() {
        ResearchCall call = new ResearchCall(1, "Call 1", "Description", FIXED_TODAY, FIXED_FUTURE_10D,
                CallStatus.CLOSED, null, null);
        when(saveCallPort.findById(1)).thenReturn(java.util.Optional.of(call));

        CallResponse response = callInteractor.getCallById(1);
        assertEquals(1, response.getId());
        assertEquals("CERRADA", response.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenGetCallByIdNotFound() {
        when(saveCallPort.findById(99)).thenReturn(java.util.Optional.empty());
        var ex = assertThrows(BusinessRuleValidationException.class, () -> callInteractor.getCallById(99));
        assertNotNull(ex);
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        ResearchCall call = new ResearchCall(1, "Call 1", "Description", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.OPEN,
                null, null);
        when(saveCallPort.findById(1)).thenReturn(java.util.Optional.of(call));

        ResearchCall updatedCall = new ResearchCall(1, "Call 1", "Description", FIXED_TODAY, FIXED_FUTURE_10D,
                CallStatus.FINISHED, null, null);
        when(saveCallPort.save(any())).thenReturn(updatedCall);

        CallResponse response = callInteractor.updateStatus(1, "FINALIZADA");
        assertEquals("FINALIZADA", response.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenUpdateStatusInvalid() {
        ResearchCall call = new ResearchCall(1, "Call 1", "Description", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.OPEN,
                null, null);
        when(saveCallPort.findById(1)).thenReturn(java.util.Optional.of(call));

        var ex = assertThrows(BusinessRuleValidationException.class, () -> callInteractor.updateStatus(1, "INVALID_STATUS"));
        assertNotNull(ex);
    }

    @Test
    void shouldThrowExceptionWhenUpdateStatusNotFound() {
        when(saveCallPort.findById(99)).thenReturn(java.util.Optional.empty());
        var ex = assertThrows(BusinessRuleValidationException.class, () -> callInteractor.updateStatus(99, "CERRADA"));
        assertNotNull(ex);
    }

    @Test
    void shouldGetVigentCallsSuccessfully() {
        ResearchCall call1 = new ResearchCall(1, "Call 1", "Description", FIXED_TODAY, FIXED_FUTURE_10D,
                CallStatus.OPEN, null, null);
        ResearchCall call2 = new ResearchCall(2, "Call 2", "Description", FIXED_TODAY, FIXED_FUTURE_10D,
                CallStatus.OPEN, null, null);

        when(saveCallPort.findByStatus(CallStatus.OPEN)).thenReturn(Arrays.asList(call1, call2));

        List<CallResponse> vigentCalls = callInteractor.getVigentCalls();
        assertEquals(2, vigentCalls.size());
        assertEquals("Call 1", vigentCalls.get(0).getTitle());
        assertEquals("Call 2", vigentCalls.get(1).getTitle());
    }

    @Test
    void shouldReturnEmptyListWhenNoVigentCalls() {
        when(saveCallPort.findByStatus(CallStatus.OPEN)).thenReturn(Collections.emptyList());

        List<CallResponse> vigentCalls = callInteractor.getVigentCalls();
        assertTrue(vigentCalls.isEmpty());
    }

    @Test
    void shouldThrowWhenEndDateBeforeStartDate() {
        var ex = assertThrows(BusinessRuleValidationException.class, () ->
                new ResearchCall(1, "Bad", "Desc", FIXED_FUTURE_2M, FIXED_PAST_5, CallStatus.OPEN, null, null));
        assertNotNull(ex);
    }
}