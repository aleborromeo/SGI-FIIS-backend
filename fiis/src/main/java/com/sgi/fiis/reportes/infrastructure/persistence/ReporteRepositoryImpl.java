package com.sgi.fiis.reportes.infrastructure.persistence;

import com.sgi.fiis.reportes.domain.model.*;
import com.sgi.fiis.reportes.domain.repository.ReporteRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación JdbcTemplate del repositorio de reportes institucionales (RF-94, RF-95).
 * Utiliza construcción dinámica de SQL para aplicar filtros combinables sin romper los resultados.
 */
@Repository
public class ReporteRepositoryImpl implements ReporteRepositoryPort {

    private final JdbcTemplate jdbc;

    /** Nombre de columna reutilizado en múltiples RowMappers. */
    private static final String COL_NOMBRE_GRUPO = "nombre_grupo";

    public ReporteRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // =========================================================================
    // REPORTE DE PROYECTOS
    // =========================================================================

    private static final String SQL_PROYECTOS_BASE =
        """
        SELECT
            p.id_proyecto,
            p.codigo_proyecto,
            p.titulo_proyecto,
            p.estado_proyecto,
            g.nombre_grupo,
            l.nombre_linea,
            (u.nombres || ' ' || u.apellidos) AS nombre_responsable,
            c.titulo_convocatoria,
            p.presupuesto,
            p.fecha_inicio,
            p.fecha_fin,
            p.fecha_creacion
        FROM proyectos p
        JOIN grupos_investigacion g ON g.id_grupo = p.id_grupo
        JOIN lineas_investigacion l ON l.id_linea = p.id_linea
        JOIN usuarios u             ON u.id_usuario = p.id_responsable
        LEFT JOIN convocatorias c   ON c.id_convocatoria = p.id_convocatoria
        """;

    @Override
    @SuppressWarnings("java:S2077") // SQL dinámico seguro: los valores se pasan como parámetros posicionales a JdbcTemplate
    public List<ReporteProyecto> findProyectos(FiltroReporte f) {
        QueryBuilder qb = buildProyectosWhere(f);
        String sql = SQL_PROYECTOS_BASE + qb.where +
                     " ORDER BY p.fecha_creacion DESC" +
                     " LIMIT ? OFFSET ?";
        qb.params.add(f.getSize());
        qb.params.add(f.getOffset());
        return jdbc.query(sql, MAPPER_PROYECTO, qb.params.toArray());
    }

    @Override
    @SuppressWarnings("java:S2077") // SQL dinámico seguro: los valores se pasan como parámetros posicionales a JdbcTemplate
    public long countProyectos(FiltroReporte f) {
        QueryBuilder qb = buildProyectosWhere(f);
        String sql = "SELECT COUNT(*) FROM proyectos p " +
                     "JOIN grupos_investigacion g ON g.id_grupo = p.id_grupo " +
                     "JOIN lineas_investigacion l ON l.id_linea = p.id_linea " +
                     "JOIN usuarios u ON u.id_usuario = p.id_responsable " +
                     "LEFT JOIN convocatorias c ON c.id_convocatoria = p.id_convocatoria " +
                     qb.where;
        Long count = jdbc.queryForObject(sql, Long.class, qb.params.toArray());
        return count != null ? count : 0L;
    }

    private QueryBuilder buildProyectosWhere(FiltroReporte f) {
        QueryBuilder qb = new QueryBuilder();
        if (f.getIdGrupo() != null)       qb.and("p.id_grupo = ?",           f.getIdGrupo());
        if (f.getEstado() != null)         qb.and("p.estado_proyecto = ?",    f.getEstado());
        if (f.getFechaDesde() != null)     qb.and("p.fecha_creacion >= ?",    Date.valueOf(f.getFechaDesde()));
        if (f.getFechaHasta() != null)     qb.and("p.fecha_creacion <= ?",    Date.valueOf(f.getFechaHasta()));
        if (f.getIdInvestigador() != null) qb.and("p.id_responsable = ?",     f.getIdInvestigador());
        if (f.getIdConvocatoria() != null) qb.and("p.id_convocatoria = ?",    f.getIdConvocatoria());
        return qb;
    }

    private static final RowMapper<ReporteProyecto> MAPPER_PROYECTO = (rs, rn) -> {
        ReporteProyecto r = new ReporteProyecto();
        r.setIdProyecto(rs.getInt("id_proyecto"));
        r.setCodigoProyecto(rs.getString("codigo_proyecto"));
        r.setTituloProyecto(rs.getString("titulo_proyecto"));
        r.setEstadoProyecto(rs.getString("estado_proyecto"));
        r.setNombreGrupo(rs.getString(COL_NOMBRE_GRUPO));
        r.setNombreLinea(rs.getString("nombre_linea"));
        r.setNombreResponsable(rs.getString("nombre_responsable"));
        r.setTituloConvocatoria(rs.getString("titulo_convocatoria"));
        r.setPresupuesto(rs.getBigDecimal("presupuesto"));
        Date fi = rs.getDate("fecha_inicio");
        if (fi != null) r.setFechaInicio(fi.toLocalDate());
        Date ff = rs.getDate("fecha_fin");
        if (ff != null) r.setFechaFin(ff.toLocalDate());
        java.sql.Timestamp fc = rs.getTimestamp("fecha_creacion");
        if (fc != null) r.setFechaCreacion(fc.toLocalDateTime());
        return r;
    };

    // =========================================================================
    // REPORTE DE TRÁMITES
    // =========================================================================

    private static final String SQL_TRAMITES_BASE =
        """
        SELECT
            t.id_tramite,
            t.codigo_tramite,
            t.tipo_tramite,
            (u.nombres || ' ' || u.apellidos) AS nombre_solicitante,
            t.estado_actual,
            t.rol_revisor_actual,
            g.nombre_grupo,
            t.fecha_envio,
            t.fecha_actualizacion
        FROM tramites t
        JOIN usuarios u             ON u.id_usuario = t.id_solicitante
        JOIN grupos_investigacion g ON g.id_grupo   = t.id_grupo
        """;

    @Override
    @SuppressWarnings("java:S2077") // SQL dinámico seguro: los valores se pasan como parámetros posicionales a JdbcTemplate
    public List<ReporteTramite> findTramites(FiltroReporte f) {
        QueryBuilder qb = buildTramitesWhere(f);
        String sql = SQL_TRAMITES_BASE + qb.where +
                     " ORDER BY t.fecha_envio DESC" +
                     " LIMIT ? OFFSET ?";
        qb.params.add(f.getSize());
        qb.params.add(f.getOffset());
        return jdbc.query(sql, MAPPER_TRAMITE, qb.params.toArray());
    }

    @Override
    @SuppressWarnings("java:S2077") // SQL dinámico seguro: los valores se pasan como parámetros posicionales a JdbcTemplate
    public long countTramites(FiltroReporte f) {
        QueryBuilder qb = buildTramitesWhere(f);
        String sql = "SELECT COUNT(*) FROM tramites t " +
                     "JOIN usuarios u ON u.id_usuario = t.id_solicitante " +
                     "JOIN grupos_investigacion g ON g.id_grupo = t.id_grupo " +
                     qb.where;
        Long count = jdbc.queryForObject(sql, Long.class, qb.params.toArray());
        return count != null ? count : 0L;
    }

    private QueryBuilder buildTramitesWhere(FiltroReporte f) {
        QueryBuilder qb = new QueryBuilder();
        if (f.getIdGrupo() != null)       qb.and("t.id_grupo = ?",           f.getIdGrupo());
        if (f.getEstado() != null)         qb.and("t.estado_actual = ?",      f.getEstado());
        if (f.getFechaDesde() != null)     qb.and("t.fecha_envio >= ?",       Date.valueOf(f.getFechaDesde()));
        if (f.getFechaHasta() != null)     qb.and("t.fecha_envio <= ?",       Date.valueOf(f.getFechaHasta()));
        if (f.getIdInvestigador() != null) qb.and("t.id_solicitante = ?",     f.getIdInvestigador());
        if (f.getTipoTramite() != null)    qb.and("t.tipo_tramite = ?",       f.getTipoTramite());
        return qb;
    }

    private static final RowMapper<ReporteTramite> MAPPER_TRAMITE = (rs, rn) -> {
        ReporteTramite r = new ReporteTramite();
        r.setIdTramite(rs.getInt("id_tramite"));
        r.setCodigoTramite(rs.getString("codigo_tramite"));
        r.setTipoTramite(rs.getString("tipo_tramite"));
        r.setNombreSolicitante(rs.getString("nombre_solicitante"));
        r.setEstadoActual(rs.getString("estado_actual"));
        r.setRolRevisorActual(rs.getString("rol_revisor_actual"));
        r.setNombreGrupo(rs.getString(COL_NOMBRE_GRUPO));
        java.sql.Timestamp fe = rs.getTimestamp("fecha_envio");
        if (fe != null) r.setFechaEnvio(fe.toLocalDateTime());
        java.sql.Timestamp fa = rs.getTimestamp("fecha_actualizacion");
        if (fa != null) r.setFechaActualizacion(fa.toLocalDateTime());
        return r;
    };

    // =========================================================================
    // REPORTE DE RESOLUCIONES
    // =========================================================================

    private static final String SQL_RESOLUCIONES_BASE =
        """
        SELECT
            r.id_resolucion,
            r.numero_resolucion,
            r.fecha_emision,
            r.asunto,
            t.codigo_tramite,
            t.tipo_tramite,
            (u.nombres || ' ' || u.apellidos) AS nombre_solicitante,
            r.fecha_registro
        FROM resoluciones r
        JOIN tramites t ON t.id_tramite  = r.id_tramite
        JOIN usuarios u ON u.id_usuario  = t.id_solicitante
        """;

    @Override
    @SuppressWarnings("java:S2077") // SQL dinámico seguro: los valores se pasan como parámetros posicionales a JdbcTemplate
    public List<ReporteResolucion> findResoluciones(FiltroReporte f) {
        QueryBuilder qb = buildResolucionesWhere(f);
        String sql = SQL_RESOLUCIONES_BASE + qb.where +
                     " ORDER BY r.fecha_emision DESC" +
                     " LIMIT ? OFFSET ?";
        qb.params.add(f.getSize());
        qb.params.add(f.getOffset());
        return jdbc.query(sql, MAPPER_RESOLUCION, qb.params.toArray());
    }

    @Override
    @SuppressWarnings("java:S2077") // SQL dinámico seguro: los valores se pasan como parámetros posicionales a JdbcTemplate
    public long countResoluciones(FiltroReporte f) {
        QueryBuilder qb = buildResolucionesWhere(f);
        String sql = "SELECT COUNT(*) FROM resoluciones r " +
                     "JOIN tramites t ON t.id_tramite = r.id_tramite " +
                     "JOIN usuarios u ON u.id_usuario = t.id_solicitante " +
                     qb.where;
        Long count = jdbc.queryForObject(sql, Long.class, qb.params.toArray());
        return count != null ? count : 0L;
    }

    private QueryBuilder buildResolucionesWhere(FiltroReporte f) {
        QueryBuilder qb = new QueryBuilder();
        if (f.getFechaDesde() != null)     qb.and("r.fecha_emision >= ?",     Date.valueOf(f.getFechaDesde()));
        if (f.getFechaHasta() != null)     qb.and("r.fecha_emision <= ?",     Date.valueOf(f.getFechaHasta()));
        if (f.getIdInvestigador() != null) qb.and("t.id_solicitante = ?",     f.getIdInvestigador());
        if (f.getTipoTramite() != null)    qb.and("t.tipo_tramite = ?",       f.getTipoTramite());
        return qb;
    }

    private static final RowMapper<ReporteResolucion> MAPPER_RESOLUCION = (rs, rn) -> {
        ReporteResolucion r = new ReporteResolucion();
        r.setIdResolucion(rs.getInt("id_resolucion"));
        r.setNumeroResolucion(rs.getString("numero_resolucion"));
        Date fe = rs.getDate("fecha_emision");
        if (fe != null) r.setFechaEmision(fe.toLocalDate());
        r.setAsunto(rs.getString("asunto"));
        r.setCodigoTramite(rs.getString("codigo_tramite"));
        r.setTipoTramite(rs.getString("tipo_tramite"));
        r.setNombreSolicitante(rs.getString("nombre_solicitante"));
        java.sql.Timestamp fr = rs.getTimestamp("fecha_registro");
        if (fr != null) r.setFechaRegistro(fr.toLocalDateTime());
        return r;
    };

    // =========================================================================
    // REPORTE DE INFORMES DE AVANCE
    // =========================================================================

    private static final String SQL_INFORMES_BASE =
        """
        SELECT
            ia.id_informe,
            p.codigo_proyecto,
            p.titulo_proyecto,
            ia.tipo_informe,
            ia.periodo,
            ia.porcentaje_avance,
            ia.estado_informe,
            g.nombre_grupo,
            ia.fecha_registro
        FROM informes_avance ia
        JOIN proyectos p            ON p.id_proyecto = ia.id_proyecto
        JOIN grupos_investigacion g ON g.id_grupo    = p.id_grupo
        """;

    @Override
    @SuppressWarnings("java:S2077") // SQL dinámico seguro: los valores se pasan como parámetros posicionales a JdbcTemplate
    public List<ReporteInformeAvance> findInformesAvance(FiltroReporte f) {
        QueryBuilder qb = buildInformesWhere(f);
        String sql = SQL_INFORMES_BASE + qb.where +
                     " ORDER BY ia.fecha_registro DESC" +
                     " LIMIT ? OFFSET ?";
        qb.params.add(f.getSize());
        qb.params.add(f.getOffset());
        return jdbc.query(sql, MAPPER_INFORME, qb.params.toArray());
    }

    @Override
    @SuppressWarnings("java:S2077") // SQL dinámico seguro: los valores se pasan como parámetros posicionales a JdbcTemplate
    public long countInformesAvance(FiltroReporte f) {
        QueryBuilder qb = buildInformesWhere(f);
        String sql = "SELECT COUNT(*) FROM informes_avance ia " +
                     "JOIN proyectos p ON p.id_proyecto = ia.id_proyecto " +
                     "JOIN grupos_investigacion g ON g.id_grupo = p.id_grupo " +
                     qb.where;
        Long count = jdbc.queryForObject(sql, Long.class, qb.params.toArray());
        return count != null ? count : 0L;
    }

    private QueryBuilder buildInformesWhere(FiltroReporte f) {
        QueryBuilder qb = new QueryBuilder();
        if (f.getIdGrupo() != null)    qb.and("p.id_grupo = ?",            f.getIdGrupo());
        if (f.getEstado() != null)     qb.and("ia.estado_informe = ?",     f.getEstado());
        if (f.getFechaDesde() != null) qb.and("ia.fecha_registro >= ?",    Date.valueOf(f.getFechaDesde()));
        if (f.getFechaHasta() != null) qb.and("ia.fecha_registro <= ?",    Date.valueOf(f.getFechaHasta()));
        if (f.getTipoTramite() != null) qb.and("ia.tipo_informe = ?",      f.getTipoTramite());
        return qb;
    }

    private static final RowMapper<ReporteInformeAvance> MAPPER_INFORME = (rs, rn) -> {
        ReporteInformeAvance r = new ReporteInformeAvance();
        r.setIdInforme(rs.getInt("id_informe"));
        r.setCodigoProyecto(rs.getString("codigo_proyecto"));
        r.setTituloProyecto(rs.getString("titulo_proyecto"));
        r.setTipoInforme(rs.getString("tipo_informe"));
        r.setPeriodo(rs.getString("periodo"));
        r.setPorcentajeAvance(rs.getBigDecimal("porcentaje_avance"));
        r.setEstadoInforme(rs.getString("estado_informe"));
        r.setNombreGrupo(rs.getString(COL_NOMBRE_GRUPO));
        java.sql.Timestamp fr = rs.getTimestamp("fecha_registro");
        if (fr != null) r.setFechaRegistro(fr.toLocalDateTime());
        return r;
    };

    // =========================================================================
    // Helper interno: construcción dinámica de cláusulas WHERE
    // =========================================================================

    /**
     * Acumula condiciones WHERE y parámetros posicionales para JdbcTemplate.
     */
    private static class QueryBuilder {
        final StringBuilder where  = new StringBuilder();
        final List<Object>  params = new ArrayList<>();
        boolean first = true;

        void and(String condition, Object value) {
            where.append(first ? " WHERE " : " AND ").append(condition);
            params.add(value);
            first = false;
        }
    }
}
