package com.sgi.fiis.shared.infrastructure.persistence;

import com.sgi.fiis.shared.application.dto.AuditLogEntryDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AuditLogRepository auditLogRepository;

    @Test
    void testGetStats() {
        when(jdbcTemplate.queryForObject(contains("COUNT(*) FROM auditoria_general"), eq(Long.class))).thenReturn(10L);
        when(jdbcTemplate.queryForObject(contains("DISTINCT tabla_afectada"), eq(Long.class))).thenReturn(3L);
        when(jdbcTemplate.queryForObject(contains("DISTINCT id_usuario"), eq(Long.class))).thenReturn(2L);
        when(jdbcTemplate.queryForObject(contains("CURRENT_DATE"), eq(Long.class))).thenReturn(4L);

        Map<String, Object> stats = auditLogRepository.getStats();

        assertNotNull(stats);
        assertEquals(10L, stats.get("totalRecords"));
        assertEquals(3L, stats.get("tablesAffected"));
        assertEquals(2L, stats.get("activeUsers"));
        assertEquals(4L, stats.get("todayActions"));
    }

    @Test
    void testGetStatsWithNulls() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(null);

        Map<String, Object> stats = auditLogRepository.getStats();

        assertNotNull(stats);
        assertEquals(0L, stats.get("totalRecords"));
        assertEquals(0L, stats.get("tablesAffected"));
        assertEquals(0L, stats.get("activeUsers"));
        assertEquals(0L, stats.get("todayActions"));
    }

    @Test
    void testFindAll() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id_auditoria")).thenReturn(1L);
        when(rs.getString("tabla_afectada")).thenReturn("users");
        when(rs.getLong("id_registro")).thenReturn(100L);
        when(rs.getString("accion")).thenReturn("INSERT");
        when(rs.getLong("id_usuario")).thenReturn(5L);
        when(rs.getString("nombre_usuario")).thenReturn("Juan Perez");
        when(rs.getString("datos_anteriores")).thenReturn("{}");
        when(rs.getString("datos_nuevos")).thenReturn("{\"id\": 1}");
        when(rs.getString("ip_origen")).thenReturn("127.0.0.1");
        
        LocalDateTime now = LocalDateTime.now();
        when(rs.getTimestamp("fecha_accion")).thenReturn(Timestamp.valueOf(now));

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    RowMapper<AuditLogEntryDTO> rm = invocation.getArgument(1);
                    AuditLogEntryDTO dto = rm.mapRow(rs, 0);
                    return List.of(dto);
                });

        List<AuditLogEntryDTO> results = auditLogRepository.findAll(0, 10);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("users", results.get(0).getTablaAfectada());
        assertEquals(100L, results.get(0).getIdRegistro());
        assertEquals("INSERT", results.get(0).getAccion());
        assertEquals(5L, results.get(0).getIdUsuario());
        assertEquals("Juan Perez", results.get(0).getNombreUsuario());
        assertEquals("{}", results.get(0).getDatosAnteriores());
        assertEquals("{\"id\": 1}", results.get(0).getDatosNuevos());
        assertEquals("127.0.0.1", results.get(0).getIpOrigen());
        assertEquals(now, results.get(0).getFechaAccion());
    }

    @Test
    void testFindByTabla() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id_auditoria")).thenReturn(2L);
        when(rs.getString("tabla_afectada")).thenReturn("projects");
        when(rs.getTimestamp("fecha_accion")).thenReturn(null);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    RowMapper<AuditLogEntryDTO> rm = invocation.getArgument(1);
                    AuditLogEntryDTO dto = rm.mapRow(rs, 0);
                    return List.of(dto);
                });

        List<AuditLogEntryDTO> results = auditLogRepository.findByTabla("projects", 0, 10);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(2L, results.get(0).getId());
        assertNull(results.get(0).getFechaAccion());
    }

    @Test
    void testFindByIdRegistro() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id_auditoria")).thenReturn(3L);
        when(rs.getLong("id_registro")).thenReturn(500L);
        when(rs.getTimestamp("fecha_accion")).thenReturn(null);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong(), anyInt(), anyInt()))
                .thenAnswer(invocation -> {
                    RowMapper<AuditLogEntryDTO> rm = invocation.getArgument(1);
                    AuditLogEntryDTO dto = rm.mapRow(rs, 0);
                    return List.of(dto);
                });

        List<AuditLogEntryDTO> results = auditLogRepository.findByIdRegistro(500L, 0, 10);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(3L, results.get(0).getId());
        assertEquals(500L, results.get(0).getIdRegistro());
    }
}
