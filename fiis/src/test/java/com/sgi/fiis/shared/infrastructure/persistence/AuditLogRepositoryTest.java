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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

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
    @SuppressWarnings("unchecked")
    void findAllShouldMapRow() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id_auditoria")).thenReturn(1L);
        when(rs.getString("tabla_afectada")).thenReturn("tramites");
        when(rs.getLong("id_registro")).thenReturn(100L);
        when(rs.getString("accion")).thenReturn("CREAR");
        when(rs.getLong("id_usuario")).thenReturn(10L);
        when(rs.getString("nombre_usuario")).thenReturn("Juan Perez");
        when(rs.getString("datos_anteriores")).thenReturn(null);
        when(rs.getString("datos_nuevos")).thenReturn("{\"key\":\"val\"}");
        when(rs.getString("ip_origen")).thenReturn("192.168.1.1");
        when(rs.getTimestamp("fecha_accion")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, Month.JANUARY, 15, 10, 0)));

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt())).thenAnswer(invocation -> {
            RowMapper<AuditLogEntryDTO> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        var result = auditLogRepository.findAll(0, 20);

        assertEquals(1, result.size());
        var dto = result.get(0);
        assertEquals(1L, dto.getId());
        assertEquals("tramites", dto.getTablaAfectada());
        assertEquals(100L, dto.getIdRegistro());
        assertEquals("CREAR", dto.getAccion());
        assertEquals(10L, dto.getIdUsuario());
        assertEquals("Juan Perez", dto.getNombreUsuario());
        assertNull(dto.getDatosAnteriores());
        assertEquals("{\"key\":\"val\"}", dto.getDatosNuevos());
        assertEquals("192.168.1.1", dto.getIpOrigen());
        assertEquals(LocalDateTime.of(2026, 1, 15, 10, 0), dto.getFechaAccion());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllShouldHandleNullTimestamp() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id_auditoria")).thenReturn(1L);
        when(rs.getString("tabla_afectada")).thenReturn("tramites");
        when(rs.getLong("id_registro")).thenReturn(100L);
        when(rs.getString("accion")).thenReturn("CREAR");
        when(rs.getLong("id_usuario")).thenReturn(10L);
        when(rs.getString("nombre_usuario")).thenReturn("Juan Perez");
        when(rs.getString("datos_anteriores")).thenReturn(null);
        when(rs.getString("datos_nuevos")).thenReturn(null);
        when(rs.getString("ip_origen")).thenReturn(null);
        when(rs.getTimestamp("fecha_accion")).thenReturn(null);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt())).thenAnswer(invocation -> {
            RowMapper<AuditLogEntryDTO> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        var result = auditLogRepository.findAll(0, 20);

        assertEquals(1, result.size());
        assertNull(result.get(0).getFechaAccion());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAllShouldReturnEmptyWhenNoResults() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyInt(), anyInt()))
                .thenReturn(List.of());

        var result = auditLogRepository.findAll(1, 20);

        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByTablaShouldMapRow() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id_auditoria")).thenReturn(2L);
        when(rs.getString("tabla_afectada")).thenReturn("proyectos");
        when(rs.getLong("id_registro")).thenReturn(200L);
        when(rs.getString("accion")).thenReturn("EDITAR");
        when(rs.getLong("id_usuario")).thenReturn(20L);
        when(rs.getString("nombre_usuario")).thenReturn("Ana Lopez");
        when(rs.getString("datos_anteriores")).thenReturn("old");
        when(rs.getString("datos_nuevos")).thenReturn("new");
        when(rs.getString("ip_origen")).thenReturn("10.0.0.1");
        when(rs.getTimestamp("fecha_accion")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt(), anyInt())).thenAnswer(invocation -> {
            RowMapper<AuditLogEntryDTO> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        var result = auditLogRepository.findByTabla("proyectos", 0, 20);

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals("proyectos", result.get(0).getTablaAfectada());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByTablaShouldReturnEmptyWhenNoResults() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        var result = auditLogRepository.findByTabla("nonexistent", 0, 20);

        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByIdRegistroShouldMapRow() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id_auditoria")).thenReturn(3L);
        when(rs.getString("tabla_afectada")).thenReturn("usuarios");
        when(rs.getLong("id_registro")).thenReturn(300L);
        when(rs.getString("accion")).thenReturn("ELIMINAR");
        when(rs.getLong("id_usuario")).thenReturn(30L);
        when(rs.getString("nombre_usuario")).thenReturn("Carlos Ruiz");
        when(rs.getString("datos_anteriores")).thenReturn(null);
        when(rs.getString("datos_nuevos")).thenReturn(null);
        when(rs.getString("ip_origen")).thenReturn(null);
        when(rs.getTimestamp("fecha_accion")).thenReturn(null);

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong(), anyInt(), anyInt())).thenAnswer(invocation -> {
            RowMapper<AuditLogEntryDTO> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        var result = auditLogRepository.findByIdRegistro(300L, 0, 20);

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getId());
        assertEquals(300L, result.get(0).getIdRegistro());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByIdRegistroShouldReturnEmptyWhenNoResults() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of());

        var result = auditLogRepository.findByIdRegistro(999L, 0, 20);

        assertEquals(0, result.size());
    }
}
