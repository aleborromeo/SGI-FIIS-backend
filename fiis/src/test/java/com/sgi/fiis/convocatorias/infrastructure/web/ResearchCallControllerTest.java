package com.sgi.fiis.convocatorias.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.in.CreateCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.GetCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.UpdateCallStatusUseCase;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*; // Add verify, times
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("all")
class ResearchCallControllerTest {

    private static final LocalDate FIXED_START = LocalDate.of(2026, Month.JUNE, 1);
    private static final LocalDate FIXED_END = LocalDate.of(2026, Month.DECEMBER, 1);

    private MockMvc mockMvc;
    private CreateCallUseCase createCallUseCase;
    private GetCallUseCase getCallUseCase;
    private UpdateCallStatusUseCase updateCallStatusUseCase;
    private MembershipRepositoryPort membershipRepositoryPort;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        createCallUseCase = mock(CreateCallUseCase.class);
        getCallUseCase = mock(GetCallUseCase.class);
        updateCallStatusUseCase = mock(UpdateCallStatusUseCase.class);
        membershipRepositoryPort = mock(MembershipRepositoryPort.class);
        ResearchCallController controller = new ResearchCallController(createCallUseCase, getCallUseCase, updateCallStatusUseCase, membershipRepositoryPort);
        MessageSource messageSource = mock(MessageSource.class);
        lenient().when(messageSource.getMessage(anyString(), any(), anyString(), any())).thenAnswer(inv -> inv.getArgument(2));
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(messageSource))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(Long userId, String username, String role) {
        CustomUserDetails userDetails = new CustomUserDetails(
                userId, username, "password", true,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldCreateCall() throws Exception {
        authenticateAs(1L, "admin@unas.edu.pe", "ADMIN");
        CreateCallRequest request = new CreateCallRequest();
        request.setTitle("Call Test");
        request.setDescription("Description");
        request.setStartDate(FIXED_START);
        request.setEndDate(FIXED_END);
        request.setResearchLineIds(Collections.singletonList(1));

        CallResponse response = new CallResponse(1, "Call Test", "Description", FIXED_START, FIXED_END, "ABIERTA", null, null);

        when(createCallUseCase.execute(any(CreateCallRequest.class), anyInt())).thenReturn(response);

        mockMvc.perform(post("/api/v1/calls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Call Test"))
                .andExpect(jsonPath("$.status").value("ABIERTA"));

        verify(createCallUseCase, times(1)).execute(any(CreateCallRequest.class), anyInt());
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

    @Test
    void shouldGetVigentCalls() throws Exception {
        CallResponse call1 = new CallResponse(1, "Call 1", "Desc", FIXED_START, FIXED_END, "ABIERTA", null, null);
        CallResponse call2 = new CallResponse(2, "Call 2", "Desc", FIXED_START, FIXED_END, "ABIERTA", null, null);
        when(getCallUseCase.getVigentCalls()).thenReturn(List.of(call1, call2));

        mockMvc.perform(get("/api/v1/calls/vigent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Call 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Call 2"));

        verify(getCallUseCase, times(1)).getVigentCalls();
    }

    @Test
    void shouldReturnEmptyListWhenNoVigentCalls() throws Exception {
        when(getCallUseCase.getVigentCalls()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/calls/vigent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(getCallUseCase, times(1)).getVigentCalls();
    }

    @Test
    void shouldCheckPrerequisitosWhenAllMet() throws Exception {
        authenticateAs(1L, "docente@unas.edu.pe", "DOCENTE_INVESTIGADOR");
        when(membershipRepositoryPort.existsActiveByUser(1)).thenReturn(true);
        when(getCallUseCase.getVigentCalls()).thenReturn(List.of(
                new CallResponse(1, "Call 1", "Desc", FIXED_START, FIXED_END, "ABIERTA", null, null)
        ));

        mockMvc.perform(get("/api/v1/calls/prerequisitos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasActiveGroup").value(true))
                .andExpect(jsonPath("$.hasVigentCalls").value(true))
                .andExpect(jsonPath("$.docente").value(true))
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    void shouldCheckPrerequisitosWhenNoneMet() throws Exception {
        authenticateAs(2L, "estudiante@unas.edu.pe", "ESTUDIANTE");
        when(membershipRepositoryPort.existsActiveByUser(2)).thenReturn(false);
        when(getCallUseCase.getVigentCalls()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/calls/prerequisitos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasActiveGroup").value(false))
                .andExpect(jsonPath("$.hasVigentCalls").value(false))
                .andExpect(jsonPath("$.docente").value(false))
                .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    void shouldCheckPrerequisitosWhenOnlyDocente() throws Exception {
        authenticateAs(3L, "docente2@unas.edu.pe", "DOCENTE_INVESTIGADOR");
        when(membershipRepositoryPort.existsActiveByUser(3)).thenReturn(false);
        when(getCallUseCase.getVigentCalls()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/calls/prerequisitos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasActiveGroup").value(false))
                .andExpect(jsonPath("$.hasVigentCalls").value(false))
                .andExpect(jsonPath("$.docente").value(true))
                .andExpect(jsonPath("$.valid").value(false));
    }
}
