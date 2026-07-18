package com.sgi.fiis.shared.infrastructure.persistence;

import com.sgi.fiis.shared.application.dto.AuditLogEntryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AuditLogRepository auditLogRepository;

    @Test
    void findAllShouldReturnList() {
        var dto = AuditLogEntryDTO.builder().id(1L).tablaAfectada("tramites").build();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        var result = auditLogRepository.findAll(0, 20);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("tramites", result.get(0).getTablaAfectada());
    }

    @Test
    void findAllShouldReturnEmptyWhenNoResults() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(List.of());

        var result = auditLogRepository.findAll(1, 20);

        assertEquals(0, result.size());
    }

    @Test
    void findByTablaShouldReturnList() {
        var dto = AuditLogEntryDTO.builder().id(2L).tablaAfectada("proyectos").build();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        var result = auditLogRepository.findByTabla("proyectos", 0, 20);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void findByTablaShouldReturnEmptyWhenNoResults() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        var result = auditLogRepository.findByTabla("nonexistent", 0, 20);

        assertEquals(0, result.size());
    }

    @Test
    void findByIdRegistroShouldReturnList() {
        var dto = AuditLogEntryDTO.builder().id(3L).idRegistro(100L).build();
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(), anyInt(), anyInt()))
                .thenReturn(List.of(dto));

        var result = auditLogRepository.findByIdRegistro(100L, 0, 20);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(100L, result.get(0).getIdRegistro());
    }

    @Test
    void findByIdRegistroShouldReturnEmptyWhenNoResults() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), any(), anyInt(), anyInt()))
                .thenReturn(List.of());

        var result = auditLogRepository.findByIdRegistro(999L, 0, 20);

        assertEquals(0, result.size());
    }
}
