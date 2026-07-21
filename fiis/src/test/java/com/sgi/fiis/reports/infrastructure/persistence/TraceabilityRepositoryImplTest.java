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
        when(rs.getString("rol_usuario_accion")).thenReturn("DIRECTOR_INVESTIGACION");
        when(rs.getString("accion")).thenReturn("APROBAR");
        when(rs.getString("estado_anterior")).thenReturn("EN_PROGRESO");
        when(rs.getString("estado_nuevo")).thenReturn("APROBADO");
        when(rs.getString("observacion")).thenReturn("Todo conforme");
        when(rs.getString("ip_origen")).thenReturn("192.168.1.1");
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
        when(rs.getString("rol_usuario_accion")).thenReturn("DIRECTOR_INVESTIGACION");
        when(rs.getString("accion")).thenReturn("APROBAR");
        when(rs.getString("estado_anterior")).thenReturn("EN_PROGRESO");
        when(rs.getString("estado_nuevo")).thenReturn("APROBADO");
        when(rs.getString("observacion")).thenReturn("Todo conforme");
        when(rs.getString("ip_origen")).thenReturn("192.168.1.1");
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

    @Test
    @DisplayName("Should return true when procedure belongs to group")
    void testIsProcedureInGroup_true() {
        when(jdbc.queryForObject(anyString(), eq(Boolean.class), eq(10), eq(5)))
                .thenReturn(true);

        boolean result = repository.isProcedureInGroup(10, 5);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when procedure does not belong to group")
    void testIsProcedureInGroup_false() {
        when(jdbc.queryForObject(anyString(), eq(Boolean.class), eq(10), eq(99)))
                .thenReturn(false);

        boolean result = repository.isProcedureInGroup(10, 99);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should find procedures with recent activity by group")
    @SuppressWarnings("unchecked")
    void testFindProceduresWithRecentActivity_byGroup() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_tramite")).thenReturn(1);
        when(rs.getString("codigo_tramite")).thenReturn("TRM-001");
        when(rs.getString("tipo_tramite")).thenReturn("PROYECTO");
        when(rs.getString("estado_actual")).thenReturn("PENDIENTE");
        when(rs.getInt("movement_count")).thenReturn(3);
        when(rs.getObject("last_movement_date", LocalDateTime.class)).thenReturn(LocalDateTime.of(2026, Month.JULY, 1, 10, 0));
        when(rs.getString("last_action")).thenReturn("APROBAR");
        when(rs.getString("last_user_name")).thenReturn("Juan Perez");

        when(jdbc.query(anyString(), any(RowMapper.class), anyInt(), anyInt())).thenAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        List<com.sgi.fiis.reports.domain.model.ProcedureRecentActivity> result = repository.findProceduresWithRecentActivity(5, 7);

        assertNotNull(result);
        assertEquals(1, result.size());
        var a = result.get(0);
        assertEquals(1, a.getProcedureId().intValue());
        assertEquals("TRM-001", a.getProcedureCode());
        assertEquals("PENDIENTE", a.getCurrentStatus());
        assertEquals(3, a.getMovementCount().intValue());
        assertEquals("APROBAR", a.getLastAction());
        assertEquals("Juan Perez", a.getLastUserName());
    }

    @Test
    @DisplayName("Should find procedures with recent activity without group")
    @SuppressWarnings("unchecked")
    void testFindProceduresWithRecentActivity_noGroup() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id_tramite")).thenReturn(2);
        when(rs.getString("codigo_tramite")).thenReturn("TRM-002");
        when(rs.getString("tipo_tramite")).thenReturn("TESIS");
        when(rs.getString("estado_actual")).thenReturn("APROBADO");
        when(rs.getInt("movement_count")).thenReturn(1);
        when(rs.getObject("last_movement_date", LocalDateTime.class)).thenReturn(null);
        when(rs.getString("last_action")).thenReturn("CREAR");
        when(rs.getString("last_user_name")).thenReturn("Ana Lopez");

        when(jdbc.query(anyString(), any(RowMapper.class), anyInt())).thenAnswer(invocation -> {
            RowMapper<?> mapper = invocation.getArgument(1);
            return List.of(mapper.mapRow(rs, 0));
        });

        List<com.sgi.fiis.reports.domain.model.ProcedureRecentActivity> result = repository.findProceduresWithRecentActivity(null, 7);

        assertNotNull(result);
        assertEquals(1, result.size());
        var a = result.get(0);
        assertEquals(2, a.getProcedureId().intValue());
        assertEquals("TRM-002", a.getProcedureCode());
        assertEquals("APROBADO", a.getCurrentStatus());
        assertEquals(1, a.getMovementCount().intValue());
        assertNull(a.getLastMovementDate());
    }
}
