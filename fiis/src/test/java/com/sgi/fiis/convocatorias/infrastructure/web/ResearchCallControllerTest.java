package com.sgi.fiis.convocatorias.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.in.CreateCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.GetCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.UpdateCallStatusUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResearchCallControllerTest {

    private MockMvc mockMvc;
    private CreateCallUseCase createCallUseCase;
    private GetCallUseCase getCallUseCase;
    private UpdateCallStatusUseCase updateCallStatusUseCase;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        createCallUseCase = Mockito.mock(CreateCallUseCase.class);
        getCallUseCase = Mockito.mock(GetCallUseCase.class);
        updateCallStatusUseCase = Mockito.mock(UpdateCallStatusUseCase.class);
        ResearchCallController controller = new ResearchCallController(createCallUseCase, getCallUseCase, updateCallStatusUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void shouldCreateCall() throws Exception {
        CreateCallRequest request = new CreateCallRequest();
        request.setTitle("Call Test");
        request.setDescription("Description");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(30));
        request.setResearchLineIds(Collections.singletonList(1));

        CallResponse response = new CallResponse(1, "Call Test", "Description", LocalDate.now(), LocalDate.now().plusDays(30), "ABIERTA", null, null);

        when(createCallUseCase.execute(any(CreateCallRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/calls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Call Test"))
                .andExpect(jsonPath("$.status").value("ABIERTA"));
    }
}
