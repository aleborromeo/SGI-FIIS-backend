package com.sgi.fiis.reports.presentation.controller;

import com.sgi.fiis.reports.application.service.TraceabilityService;
import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for {@link TraceabilityController}.
 * Uses standalone MockMvc to verify REST endpoints.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TraceabilityController - Web Layer Tests")
class TraceabilityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TraceabilityService traceabilityService;

    @InjectMocks
    private TraceabilityController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/traceability/{procedureId} — successful response
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/reports/traceability/10 → 200 OK with history")
    void getTraceability_withMovements_returns200() throws Exception {
        // arrange
        TraceabilityMovement mov = new TraceabilityMovement();
        mov.setMovementId(1);
        mov.setProcedureId(10);
        mov.setProcedureCode("TRM-2024-001");
        mov.setActionUserName("Juan Pérez");
        mov.setAction("CREACION");
        mov.setNewStatus("PENDIENTE");
        mov.setObservation("Trámite creado");
        mov.setMovementDate(LocalDateTime.of(2024, Month.JANUARY, 15, 9, 0));

        when(traceabilityService.getTraceability(10)).thenReturn(List.of(mov));

        // act & assert
        mockMvc.perform(get("/api/reports/traceability/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].procedureId").value(10))
                .andExpect(jsonPath("$[0].procedureCode").value("TRM-2024-001"))
                .andExpect(jsonPath("$[0].action").value("CREACION"))
                .andExpect(jsonPath("$[0].newStatus").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].actionUserName").value("Juan Pérez"));

        verify(traceabilityService, times(1)).getTraceability(10);
    }

    // -------------------------------------------------------------------------
    // Procedure without movements → empty list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/reports/traceability/999 → 200 OK with empty list")
    void getTraceability_noMovements_returns200WithEmptyList() throws Exception {
        // arrange
        when(traceabilityService.getTraceability(999)).thenReturn(Collections.emptyList());

        // act & assert
        mockMvc.perform(get("/api/reports/traceability/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(traceabilityService, times(1)).getTraceability(999);
    }

    // -------------------------------------------------------------------------
    // Multiple movements — verifies order and fields of the second
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/reports/traceability/10 → returns multiple movements in order")
    void getTraceability_multipleMovements_returnsCorrectOrder() throws Exception {
        // arrange
        TraceabilityMovement mov1 = new TraceabilityMovement();
        mov1.setMovementId(1);
        mov1.setProcedureId(10);
        mov1.setAction("CREACION");
        mov1.setNewStatus("PENDIENTE");
        mov1.setMovementDate(LocalDateTime.of(2024, Month.JANUARY, 15, 9, 0));

        TraceabilityMovement mov2 = new TraceabilityMovement();
        mov2.setMovementId(2);
        mov2.setProcedureId(10);
        mov2.setAction("APROBACION");
        mov2.setPreviousStatus("PENDIENTE");
        mov2.setNewStatus("APROBADO");
        mov2.setMovementDate(LocalDateTime.of(2024, Month.JANUARY, 16, 14, 30));

        when(traceabilityService.getTraceability(10)).thenReturn(List.of(mov1, mov2));

        // act & assert
        mockMvc.perform(get("/api/reports/traceability/10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].action").value("CREACION"))
                .andExpect(jsonPath("$[1].action").value("APROBACION"))
                .andExpect(jsonPath("$[1].previousStatus").value("PENDIENTE"))
                .andExpect(jsonPath("$[1].newStatus").value("APROBADO"));
    }

    // -------------------------------------------------------------------------
    // Verifies that the response Content-Type is application/json
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should respond with Content-Type application/json")
    void getTraceability_contentTypeIsJson() throws Exception {
        // arrange
        when(traceabilityService.getTraceability(anyInt())).thenReturn(Collections.emptyList());

        // act & assert
        mockMvc.perform(get("/api/reports/traceability/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
