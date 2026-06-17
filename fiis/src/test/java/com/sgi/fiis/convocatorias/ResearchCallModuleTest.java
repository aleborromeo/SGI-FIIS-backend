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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ResearchCallModuleTest {

    private SaveCallPort saveCallPort;
    private CreateCallInteractor createCallInteractor;
    private CallInteractor callInteractor;

    @BeforeEach
    public void setup() {
        saveCallPort = Mockito.mock(SaveCallPort.class);
        createCallInteractor = new CreateCallInteractor(saveCallPort);
        callInteractor = new CallInteractor(saveCallPort);
    }

    @Test
    public void shouldCreateCallSuccessfully() {
        CreateCallRequest request = new CreateCallRequest();
        request.setTitle("Call 2026");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusMonths(2));

        ResearchCall savedCall = new ResearchCall(1, "Call 2026", LocalDate.now(), LocalDate.now().plusMonths(2), CallStatus.OPEN);
        when(saveCallPort.save(any(ResearchCall.class))).thenReturn(savedCall);

        CallResponse response = createCallInteractor.execute(request);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Call 2026", response.getTitle());
        assertEquals("ABIERTA", response.getStatus());
    }

    @Test
    public void shouldValidateDatesCorrectlyOnSubmission() {
        // Active open call
        ResearchCall openCall = new ResearchCall(1, "Call 1", LocalDate.now().minusDays(5), LocalDate.now().plusDays(5), CallStatus.OPEN);
        assertDoesNotThrow(() -> openCall.validateCanSubmitProject(LocalDate.now()));

        // Closed call
        ResearchCall closedCall = new ResearchCall(2, "Call 2", LocalDate.now().minusDays(5), LocalDate.now().plusDays(5), CallStatus.CLOSED);
        assertThrows(BusinessRuleValidationException.class, () -> closedCall.validateCanSubmitProject(LocalDate.now()));

        // Out of date call (expired)
        ResearchCall expiredCall = new ResearchCall(3, "Call 3", LocalDate.now().minusDays(10), LocalDate.now().minusDays(2), CallStatus.OPEN);
        assertThrows(BusinessRuleValidationException.class, () -> expiredCall.validateCanSubmitProject(LocalDate.now()));
    }

    @Test
    public void shouldListCallsFilteredByStatus() {
        ResearchCall call1 = new ResearchCall(1, "Call 1", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.OPEN);
        ResearchCall call2 = new ResearchCall(2, "Call 2", LocalDate.now(), LocalDate.now().plusDays(10), CallStatus.CLOSED);

        when(saveCallPort.findByStatus(CallStatus.OPEN)).thenReturn(Arrays.asList(call1));
        when(saveCallPort.findAll()).thenReturn(Arrays.asList(call1, call2));

        List<CallResponse> openCalls = callInteractor.getCalls("ABIERTA");
        assertEquals(1, openCalls.size());
        assertEquals("Call 1", openCalls.get(0).getTitle());

        List<CallResponse> allCalls = callInteractor.getCalls(null);
        assertEquals(2, allCalls.size());
    }
}