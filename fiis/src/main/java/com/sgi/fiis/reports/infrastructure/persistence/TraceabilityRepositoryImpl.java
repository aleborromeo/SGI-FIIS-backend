package com.sgi.fiis.reports.infrastructure.persistence;

import com.sgi.fiis.reports.domain.model.ProcedureRecentActivity;
import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
import com.sgi.fiis.reports.domain.repository.TraceabilityRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TraceabilityRepositoryImpl implements TraceabilityRepositoryPort {

    private final JdbcTemplate jdbc;

    public TraceabilityRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SQL_TRACEABILITY =
        """
        SELECT
            m.id_movimiento,
            m.id_tramite,
            t.codigo_tramite,
            (u.nombres || ' ' || u.apellidos) AS nombre_usuario_accion,
            r.codigo_rol AS rol_usuario_accion,
            m.accion,
            m.estado_anterior,
            m.estado_nuevo,
            m.observacion,
            m.fecha_movimiento,
            ag.ip_origen
        FROM movimientos_tramite m
        JOIN tramites  t ON t.id_tramite   = m.id_tramite
        JOIN usuarios  u ON u.id_usuario   = m.id_usuario_accion
        JOIN roles     r ON r.id_rol       = u.id_rol_principal
        LEFT JOIN auditoria_general ag
            ON ag.id_usuario = m.id_usuario_accion
            AND ag.fecha_accion BETWEEN m.fecha_movimiento - INTERVAL '5 seconds'
                                   AND m.fecha_movimiento + INTERVAL '5 seconds'
            AND ag.tabla_afectada IN ('proyectos','convocatorias','tramites','planes_tesis','informes_avance','informes_tesis')
        WHERE m.id_tramite = ?
        ORDER BY m.fecha_movimiento DESC
        """;

    private static final String SQL_PROCEDURE_IN_GROUP =
        "SELECT COUNT(*) > 0 FROM tramites WHERE id_tramite = ? AND id_grupo = ?";

    @Override
    public List<TraceabilityMovement> findByProcedureId(Integer procedureId) {
        return jdbc.query(SQL_TRACEABILITY, MAPPER, procedureId);
    }

    @Override
    public boolean isProcedureInGroup(Integer procedureId, Integer groupId) {
        Boolean result = jdbc.queryForObject(SQL_PROCEDURE_IN_GROUP, Boolean.class, procedureId, groupId);
        return Boolean.TRUE.equals(result);
    }

    private static final String SQL_RECENT_ACTIVITY =
        """
        SELECT
            t.id_tramite,
            t.codigo_tramite,
            t.tipo_tramite,
            t.estado_actual,
            COUNT(m.id_movimiento) AS movement_count,
            MAX(m.fecha_movimiento) AS last_movement_date,
            (SELECT m2.accion FROM movimientos_tramite m2
             WHERE m2.id_tramite = t.id_tramite
             ORDER BY m2.fecha_movimiento DESC LIMIT 1) AS last_action,
            (SELECT (u2.nombres || ' ' || u2.apellidos)
             FROM movimientos_tramite m2
             JOIN usuarios u2 ON u2.id_usuario = m2.id_usuario_accion
             WHERE m2.id_tramite = t.id_tramite
             ORDER BY m2.fecha_movimiento DESC LIMIT 1) AS last_user_name
        FROM tramites t
        JOIN movimientos_tramite m ON m.id_tramite = t.id_tramite
        WHERE m.fecha_movimiento >= NOW() - (? || ' days')::INTERVAL
        GROUP BY t.id_tramite, t.codigo_tramite, t.tipo_tramite, t.estado_actual
        ORDER BY MAX(m.fecha_movimiento) DESC
        """;

    private static final String SQL_RECENT_ACTIVITY_BY_GROUP =
        SQL_RECENT_ACTIVITY.replace("GROUP BY", "AND t.id_grupo = ? GROUP BY");

    private static final RowMapper<ProcedureRecentActivity> RECENT_ACTIVITY_MAPPER = (rs, rn) -> {
        ProcedureRecentActivity a = new ProcedureRecentActivity();
        a.setProcedureId(rs.getInt("id_tramite"));
        a.setProcedureCode(rs.getString("codigo_tramite"));
        a.setProcedureType(rs.getString("tipo_tramite"));
        a.setCurrentStatus(rs.getString("estado_actual"));
        a.setMovementCount(rs.getInt("movement_count"));
        a.setLastMovementDate(rs.getObject("last_movement_date", LocalDateTime.class));
        a.setLastAction(rs.getString("last_action"));
        a.setLastUserName(rs.getString("last_user_name"));
        return a;
    };

    @Override
    public List<ProcedureRecentActivity> findProceduresWithRecentActivity(Integer groupId, int days) {
        if (groupId != null) {
            return jdbc.query(SQL_RECENT_ACTIVITY_BY_GROUP, RECENT_ACTIVITY_MAPPER, days, groupId);
        }
        return jdbc.query(SQL_RECENT_ACTIVITY, RECENT_ACTIVITY_MAPPER, days);
    }

    private static final RowMapper<TraceabilityMovement> MAPPER = (rs, rn) -> {
        TraceabilityMovement m = new TraceabilityMovement();
        m.setMovementId(rs.getInt("id_movimiento"));
        m.setProcedureId(rs.getInt("id_tramite"));
        m.setProcedureCode(rs.getString("codigo_tramite"));
        m.setActionUserName(rs.getString("nombre_usuario_accion"));
        m.setActionUserRole(rs.getString("rol_usuario_accion"));
        m.setAction(rs.getString("accion"));
        m.setPreviousStatus(rs.getString("estado_anterior"));
        m.setNewStatus(rs.getString("estado_nuevo"));
        m.setObservation(rs.getString("observacion"));
        m.setMovementDate(rs.getObject("fecha_movimiento", LocalDateTime.class));
        m.setIpOrigen(rs.getString("ip_origen"));
        return m;
    };
}
