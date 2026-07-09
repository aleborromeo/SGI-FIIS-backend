package com.sgi.fiis.reports.infrastructure.persistence;

import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
import com.sgi.fiis.reports.domain.repository.TraceabilityRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JdbcTemplate implementation of the procedure traceability repository.
 */
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
            m.accion,
            m.estado_anterior,
            m.estado_nuevo,
            m.observacion,
            m.fecha_movimiento
        FROM movimientos_tramite m
        JOIN tramites  t ON t.id_tramite   = m.id_tramite
        JOIN usuarios  u ON u.id_usuario   = m.id_usuario_accion
        WHERE m.id_tramite = ?
        ORDER BY m.fecha_movimiento ASC
        """;

    /**
     * Retrieves all movements of a procedure in ascending chronological order
     * to display the complete traceability history.
     */
    @Override
    public List<TraceabilityMovement> findByProcedureId(Integer procedureId) {
        return jdbc.query(SQL_TRACEABILITY, MAPPER, procedureId);
    }

    private static final RowMapper<TraceabilityMovement> MAPPER = (rs, rn) -> {
        TraceabilityMovement m = new TraceabilityMovement();
        m.setMovementId(rs.getInt("id_movimiento"));
        m.setProcedureId(rs.getInt("id_tramite"));
        m.setProcedureCode(rs.getString("codigo_tramite"));
        m.setActionUserName(rs.getString("nombre_usuario_accion"));
        m.setAction(rs.getString("accion"));
        m.setPreviousStatus(rs.getString("estado_anterior"));
        m.setNewStatus(rs.getString("estado_nuevo"));
        m.setObservation(rs.getString("observacion"));
        m.setMovementDate(rs.getObject("fecha_movimiento", LocalDateTime.class));
        return m;
    };
}
