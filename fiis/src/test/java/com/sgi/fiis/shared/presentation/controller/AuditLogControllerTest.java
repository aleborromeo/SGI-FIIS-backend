package com.sgi.fiis.shared.presentation.controller;

import com.sgi.fiis.shared.application.dto.AuditLogEntryDTO;
import com.sgi.fiis.shared.infrastructure.persistence.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogControllerTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogController auditLogController;

    @Test
    void listShouldReturnAuditLogs() {
        var dto = AuditLogEntryDTO.builder().id(1L).tablaAfectada("tramites").build();
        when(auditLogRepository.findAll(anyInt(), anyInt())).thenReturn(List.of(dto));

        var response = auditLogController.list(0, 20);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    void statsShouldReturnAuditLogStats() {
        Map<String, Object> expectedStats = Map.of("totalLogs", 10L, "byTable", Map.of("tramites", 5L));
        when(auditLogRepository.getStats()).thenReturn(expectedStats);

        var response = auditLogController.stats();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(expectedStats, response.getBody());
    }

    @Test
    void findByTablaShouldReturnFilteredLogs() {
        var dto = AuditLogEntryDTO.builder().id(2L).tablaAfectada("proyectos").build();
        when(auditLogRepository.findByTabla(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        var response = auditLogController.findByTabla("proyectos", 0, 20);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("proyectos", response.getBody().get(0).getTablaAfectada());
    }

    @Test
    void findByIdRegistroShouldReturnFilteredLogs() {
        var dto = AuditLogEntryDTO.builder().id(3L).idRegistro(100L).build();
        when(auditLogRepository.findByIdRegistro(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        var response = auditLogController.findByIdRegistro(100L, 0, 20);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(100L, response.getBody().get(0).getIdRegistro());
    }
}
