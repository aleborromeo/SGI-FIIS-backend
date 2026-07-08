package com.sgi.fiis.convocatorias.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.in.CreateCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.GetCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.UpdateCallStatusUseCase;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*; // Add verify, times
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResearchCallControllerTest {

    private static final LocalDate FIXED_START = LocalDate.of(2026, Month.JUNE, 1);
    private static final LocalDate FIXED_END = LocalDate.of(2026, Month.DECEMBER, 1);

    private MockMvc mockMvc;
    private CreateCallUseCase createCallUseCase;
    private GetCallUseCase getCallUseCase;
    private UpdateCallStatusUseCase updateCallStatusUseCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        createCallUseCase = mock(CreateCallUseCase.class);
        getCallUseCase = mock(GetCallUseCase.class);
        updateCallStatusUseCase = mock(UpdateCallStatusUseCase.class);
        ResearchCallController controller = new ResearchCallController(createCallUseCase, getCallUseCase, updateCallStatusUseCase);
        MessageSource messageSource = mock(MessageSource.class);
        lenient().when(messageSource.getMessage(anyString(), any(), anyString(), any())).thenAnswer(inv -> inv.getArgument(2));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void shouldCreateCall() throws Exception {
        CreateCallRequest request = new CreateCallRequest();
        request.setTitle("Call Test");
        request.setDescription("Description");
        request.setStartDate(FIXED_START);
        request.setEndDate(FIXED_END);
        request.setResearchLineIds(Collections.singletonList(1));

        CallResponse response = new CallResponse(1, "Call Test", "Description", FIXED_START, FIXED_END, "ABIERTA", null, null);

        when(createCallUseCase.execute(any(CreateCallRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/calls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Call Test"))
                .andExpect(jsonPath("$.status").value("ABIERTA"));

        verify(createCallUseCase, times(1)).execute(any(CreateCallRequest.class));
    }

    @Test
    void shouldGetAllCalls() throws Exception {
        CallResponse call = new CallResponse(1, "Call 1", "Desc", FIXED_START, FIXED_END, "ABIERTA", null, null);
        when(getCallUseCase.getCalls(null)).thenReturn(List.of(call));

        mockMvc.perform(get("/api/v1/calls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Call 1"));

        verify(getCallUseCase, times(1)).getCalls(null);
    }

    @Test
    void shouldGetCallById() throws Exception {
        CallResponse call = new CallResponse(1, "Call 1", "Desc", FIXED_START, FIXED_END, "ABIERTA", null, null);
        when(getCallUseCase.getCallById(1)).thenReturn(call);

        mockMvc.perform(get("/api/v1/calls/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Call 1"));

        verify(getCallUseCase, times(1)).getCallById(1);
    }

    @Test
    void shouldReturn404_WhenCallNotFound() throws Exception {
        when(getCallUseCase.getCallById(99))
                .thenThrow(new BusinessRuleValidationException("convocatorias.error.call-not-found", 99));

        mockMvc.perform(get("/api/v1/calls/99"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldUpdateCallStatus() throws Exception {
        CallResponse updated = new CallResponse(1, "Call", "Desc", FIXED_START, FIXED_END, "CERRADA", null, null);
        when(updateCallStatusUseCase.updateStatus(1, "CERRADA")).thenReturn(updated);

        mockMvc.perform(patch("/api/v1/calls/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CERRADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CERRADA"));

        verify(updateCallStatusUseCase, times(1)).updateStatus(1, "CERRADA");
    }
}
