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
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ResearchCallModuleTest {

    // Fixed dates to avoid system clock usage in tests (SonarCloud S5977)
    private static final LocalDate FIXED_TODAY      = LocalDate.of(2026, Month.JUNE, 1);
    private static final LocalDate FIXED_PAST_5     = LocalDate.of(2026, Month.MAY, 27);
    private static final LocalDate FIXED_PAST_10    = LocalDate.of(2026, Month.MAY, 22);
    private static final LocalDate FIXED_FUTURE_2M  = LocalDate.of(2026, Month.AUGUST, 1);
    private static final LocalDate FIXED_FUTURE_5D  = LocalDate.of(2026, Month.JUNE, 6);
    private static final LocalDate FIXED_PAST_2D    = LocalDate.of(2026, Month.MAY, 30);
    private static final LocalDate FIXED_FUTURE_10D = LocalDate.of(2026, Month.JUNE, 11);

    private SaveCallPort saveCallPort;
    private CreateCallInteractor createCallInteractor;
    private CallInteractor callInteractor;

    @BeforeEach
    void setup() {
        saveCallPort = Mockito.mock(SaveCallPort.class);
        createCallInteractor = new CreateCallInteractor(saveCallPort);
        callInteractor = new CallInteractor(saveCallPort);
    }

    @Test
    void shouldCreateCallSuccessfully() {
        CreateCallRequest request = new CreateCallRequest();
        request.setTitle("Call 2026");
        request.setStartDate(FIXED_TODAY);
        request.setEndDate(FIXED_FUTURE_2M);

        ResearchCall savedCall = new ResearchCall(1, "Call 2026", FIXED_TODAY, FIXED_FUTURE_2M, CallStatus.OPEN);
        when(saveCallPort.save(any(ResearchCall.class))).thenReturn(savedCall);

        CallResponse response = createCallInteractor.execute(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Call 2026", response.getTitle());
        assertEquals("ABIERTA", response.getStatus());
    }

    @Test
    void shouldValidateDatesCorrectlyOnSubmission() {
        // Active open call - single invocation in lambda (SonarCloud S5778)
        ResearchCall openCall = new ResearchCall(1, "Call 1", FIXED_PAST_5, FIXED_FUTURE_5D, CallStatus.OPEN);
        assertDoesNotThrow(() -> openCall.validateCanSubmitProject(FIXED_TODAY));

        // Closed call - single invocation in lambda
        ResearchCall closedCall = new ResearchCall(2, "Call 2", FIXED_PAST_5, FIXED_FUTURE_5D, CallStatus.CLOSED);
        assertThrows(BusinessRuleValidationException.class,
                () -> closedCall.validateCanSubmitProject(FIXED_TODAY));

        // Expired call - single invocation in lambda
        ResearchCall expiredCall = new ResearchCall(3, "Call 3", FIXED_PAST_10, FIXED_PAST_2D, CallStatus.OPEN);
        assertThrows(BusinessRuleValidationException.class,
                () -> expiredCall.validateCanSubmitProject(FIXED_TODAY));
    }

    @Test
    void shouldListCallsFilteredByStatus() {
        ResearchCall call1 = new ResearchCall(1, "Call 1", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.OPEN);
        ResearchCall call2 = new ResearchCall(2, "Call 2", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.CLOSED);

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
        ResearchCall call = new ResearchCall(1, "Call 1", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.OPEN);
        when(saveCallPort.findAll()).thenReturn(Arrays.asList(call));

        List<CallResponse> allCalls = callInteractor.execute("   ");
        assertEquals(1, allCalls.size());
    }

    @Test
    void shouldThrowExceptionForInvalidStatus() {
        assertThrows(BusinessRuleValidationException.class, () -> callInteractor.getCalls("INVALID_STATUS"));
    }

    @Test
    void shouldGetCallByIdSuccessfully() {
        ResearchCall call = new ResearchCall(1, "Call 1", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.CLOSED);
        when(saveCallPort.findById(1)).thenReturn(java.util.Optional.of(call));

        CallResponse response = callInteractor.getCallById(1);
        assertEquals(1, response.getId());
        assertEquals("CERRADA", response.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenGetCallByIdNotFound() {
        when(saveCallPort.findById(99)).thenReturn(java.util.Optional.empty());
        assertThrows(BusinessRuleValidationException.class, () -> callInteractor.getCallById(99));
    }

    @Test
    void shouldUpdateStatusSuccessfully() {
        ResearchCall call = new ResearchCall(1, "Call 1", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.OPEN);
        when(saveCallPort.findById(1)).thenReturn(java.util.Optional.of(call));
        
        ResearchCall updatedCall = new ResearchCall(1, "Call 1", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.FINISHED);
        when(saveCallPort.save(any())).thenReturn(updatedCall);

        CallResponse response = callInteractor.updateStatus(1, "FINALIZADA");
        assertEquals("FINALIZADA", response.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenUpdateStatusInvalid() {
        ResearchCall call = new ResearchCall(1, "Call 1", FIXED_TODAY, FIXED_FUTURE_10D, CallStatus.OPEN);
        when(saveCallPort.findById(1)).thenReturn(java.util.Optional.of(call));
        
        assertThrows(BusinessRuleValidationException.class, () -> callInteractor.updateStatus(1, "INVALID_STATUS"));
    }

    @Test
    void shouldThrowExceptionWhenUpdateStatusNotFound() {
        when(saveCallPort.findById(99)).thenReturn(java.util.Optional.empty());
        assertThrows(BusinessRuleValidationException.class, () -> callInteractor.updateStatus(99, "CERRADA"));
    }
}