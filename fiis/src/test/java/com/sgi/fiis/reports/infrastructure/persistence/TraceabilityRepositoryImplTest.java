package com.sgi.fiis.reports.infrastructure.persistence;

import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraceabilityRepositoryImpl Unit Tests")
class TraceabilityRepositoryImplTest {

    @Mock
    private JdbcTemplate jdbc;

    @InjectMocks
    private TraceabilityRepositoryImpl repository;

    @Test
    @DisplayName("Should successfully retrieve and map TraceabilityMovement by procedure ID")
    @SuppressWarnings("unchecked")
    void testFindByProcedureId() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_movimiento")).thenReturn(1);
        when(rs.getInt("id_tramite")).thenReturn(100);
        when(rs.getString("codigo_tramite")).thenReturn("TRM-100");
        when(rs.getString("nombre_usuario_accion")).thenReturn("Juan Perez");
        when(rs.getString("accion")).thenReturn("APROBAR");
        when(rs.getString("estado_anterior")).thenReturn("EN_PROGRESO");
        when(rs.getString("estado_nuevo")).thenReturn("APROBADO");
        when(rs.getString("observacion")).thenReturn("Todo conforme");
        when(rs.getObject("fecha_movimiento", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.MAY, 20, 10, 30));

        when(jdbc.query(anyString(), any(RowMapper.class), eq(100))).thenAnswer(invocation -> {
            RowMapper<TraceabilityMovement> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        List<TraceabilityMovement> result = repository.findByProcedureId(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        TraceabilityMovement m = result.get(0);
        assertEquals(1, m.getMovementId());
        assertEquals(100, m.getProcedureId());
        assertEquals("TRM-100", m.getProcedureCode());
        assertEquals("Juan Perez", m.getActionUserName());
        assertEquals("APROBAR", m.getAction());
        assertEquals("EN_PROGRESO", m.getPreviousStatus());
        assertEquals("APROBADO", m.getNewStatus());
        assertEquals("Todo conforme", m.getObservation());
        assertEquals(LocalDateTime.of(2026, Month.MAY, 20, 10, 30), m.getMovementDate());
    }

    @Test
    @DisplayName("Should map TraceabilityMovement with null timestamp correctly")
    @SuppressWarnings("unchecked")
    void testFindByProcedureIdNullTimestamp() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_movimiento")).thenReturn(1);
        when(rs.getInt("id_tramite")).thenReturn(100);
        when(rs.getString("codigo_tramite")).thenReturn("TRM-100");
        when(rs.getString("nombre_usuario_accion")).thenReturn("Juan Perez");
        when(rs.getString("accion")).thenReturn("APROBAR");
        when(rs.getString("estado_anterior")).thenReturn("EN_PROGRESO");
        when(rs.getString("estado_nuevo")).thenReturn("APROBADO");
        when(rs.getString("observacion")).thenReturn("Todo conforme");
        when(rs.getObject("fecha_movimiento", LocalDateTime.class)).thenReturn(null);

        when(jdbc.query(anyString(), any(RowMapper.class), eq(100))).thenAnswer(invocation -> {
            RowMapper<TraceabilityMovement> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        List<TraceabilityMovement> result = repository.findByProcedureId(100);

        assertNotNull(result);
        assertEquals(1, result.size());
        TraceabilityMovement m = result.get(0);
        assertNull(m.getMovementDate());
    }
}
