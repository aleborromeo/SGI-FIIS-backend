package com.sgi.fiis.reportes.infrastructure.persistence;

import com.sgi.fiis.reportes.domain.model.TrazabilidadMovimiento;
import com.sgi.fiis.reportes.domain.repository.TrazabilidadRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Implementación JdbcTemplate del repositorio de trazabilidad de trámites
 * (RF-96 a RF-99, RNF-46 a RNF-48).
 */
@Repository
public class TrazabilidadRepositoryImpl implements TrazabilidadRepositoryPort {

    private final JdbcTemplate jdbc;

    public TrazabilidadRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String SQL_TRAZABILIDAD =
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
     * Devuelve todos los movimientos de un trámite en orden cronológico
     * ascendente para mostrar la trazabilidad completa (RF-99).
     */
    @Override
    public List<TrazabilidadMovimiento> findByIdTramite(Integer idTramite) {
        return jdbc.query(SQL_TRAZABILIDAD, MAPPER, idTramite);
    }

    private static final RowMapper<TrazabilidadMovimiento> MAPPER = (rs, rn) -> {
        TrazabilidadMovimiento m = new TrazabilidadMovimiento();
        m.setIdMovimiento(rs.getInt("id_movimiento"));
        m.setIdTramite(rs.getInt("id_tramite"));
        m.setCodigoTramite(rs.getString("codigo_tramite"));
        m.setNombreUsuarioAccion(rs.getString("nombre_usuario_accion"));
        m.setAccion(rs.getString("accion"));
        m.setEstadoAnterior(rs.getString("estado_anterior"));
        m.setEstadoNuevo(rs.getString("estado_nuevo"));
        m.setObservacion(rs.getString("observacion"));
        java.sql.Timestamp fm = rs.getTimestamp("fecha_movimiento");
        if (fm != null) m.setFechaMovimiento(fm.toLocalDateTime());
        return m;
    };
}
