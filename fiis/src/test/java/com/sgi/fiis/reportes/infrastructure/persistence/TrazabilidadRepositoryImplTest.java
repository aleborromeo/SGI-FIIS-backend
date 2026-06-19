package com.sgi.fiis.reportes.infrastructure.persistence;

import com.sgi.fiis.reportes.domain.model.TrazabilidadMovimiento;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TrazabilidadRepositoryImpl Unit Tests")
class TrazabilidadRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private TrazabilidadRepositoryImpl repository;

    @Test
    @DisplayName("Should successfully retrieve and map TrazabilidadMovimiento by tramites ID")
    @SuppressWarnings("unchecked")
    void testFindByIdTramite() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_movimiento")).thenReturn(1);
        when(rs.getInt("id_tramite")).thenReturn(100);
        when(rs.getString("codigo_tramite")).thenReturn("TRM-100");
        when(rs.getString("nombre_usuario_accion")).thenReturn("Juan Perez");
        when(rs.getString("accion")).thenReturn("APROBAR");
        when(rs.getString("estado_anterior")).thenReturn("EN_PROGRESO");
        when(rs.getString("estado_nuevo")).thenReturn("APROBADO");
        when(rs.getString("observacion")).thenReturn("Todo conforme");
        when(rs.getTimestamp("fecha_movimiento")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 5, 20, 10, 30)));

        when(jdbc.query(anyString(), any(RowMapper.class), eq(100))).thenAnswer(invocation -> {
            RowMapper<TrazabilidadMovimiento> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        List<TrazabilidadMovimiento> result = repository.findByIdTramite(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        TrazabilidadMovimiento m = result.get(0);
        assertEquals(1, m.getIdMovimiento());
        assertEquals(100, m.getIdTramite());
        assertEquals("TRM-100", m.getCodigoTramite());
        assertEquals("Juan Perez", m.getNombreUsuarioAccion());
        assertEquals("APROBAR", m.getAccion());
        assertEquals("EN_PROGRESO", m.getEstadoAnterior());
        assertEquals("APROBADO", m.getEstadoNuevo());
        assertEquals("Todo conforme", m.getObservacion());
        assertEquals(LocalDateTime.of(2026, 5, 20, 10, 30), m.getFechaMovimiento());
    }

    @Test
    @DisplayName("Should map TrazabilidadMovimiento with null timestamp correctly")
    @SuppressWarnings("unchecked")
    void testFindByIdTramiteNullTimestamp() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_movimiento")).thenReturn(1);
        when(rs.getInt("id_tramite")).thenReturn(100);
        when(rs.getString("codigo_tramite")).thenReturn("TRM-100");
        when(rs.getString("nombre_usuario_accion")).thenReturn("Juan Perez");
        when(rs.getString("accion")).thenReturn("APROBAR");
        when(rs.getString("estado_anterior")).thenReturn("EN_PROGRESO");
        when(rs.getString("estado_nuevo")).thenReturn("APROBADO");
        when(rs.getString("observacion")).thenReturn("Todo conforme");
        when(rs.getTimestamp("fecha_movimiento")).thenReturn(null);

        when(jdbc.query(anyString(), any(RowMapper.class), eq(100))).thenAnswer(invocation -> {
            RowMapper<TrazabilidadMovimiento> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        List<TrazabilidadMovimiento> result = repository.findByIdTramite(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        TrazabilidadMovimiento m = result.get(0);
        assertNull(m.getFechaMovimiento());
    }
}
