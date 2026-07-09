package com.sgi.fiis.reports.infrastructure.persistence;

import com.sgi.fiis.reports.domain.model.*;
import com.sgi.fiis.reports.domain.repository.ReportRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JdbcTemplate implementation of the institutional reports repository.
 * Uses dynamic SQL construction to apply combinable filters.
 */
@Repository
public class ReportRepositoryImpl implements ReportRepositoryPort {

    private final JdbcTemplate jdbc;

    /** Reused column name across multiple RowMappers. */
    private static final String COL_GROUP_NAME = "nombre_grupo";

    public ReportRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // =========================================================================
    // PROJECT REPORTS
    // =========================================================================

    private static final String SQL_PROJECTS_BASE =
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
    @SuppressWarnings("java:S2077") // Dynamic SQL safe: values passed as positional parameters to JdbcTemplate
    public List<ProjectReport> findProjects(ReportFilter f) {
        QueryBuilder qb = buildProjectsWhere(f);
        String sql = SQL_PROJECTS_BASE + qb.where +
                     " ORDER BY p.fecha_creacion DESC" +
                     " LIMIT ? OFFSET ?";
        qb.params.add(f.getSize());
        qb.params.add(f.getOffset());
        return jdbc.query(sql, MAPPER_PROJECT, qb.params.toArray());
    }

    @Override
    @SuppressWarnings("java:S2077") // Dynamic SQL safe: values passed as positional parameters to JdbcTemplate
    public long countProjects(ReportFilter f) {
        QueryBuilder qb = buildProjectsWhere(f);
        String sql = "SELECT COUNT(*) FROM proyectos p " +
                     "JOIN grupos_investigacion g ON g.id_grupo = p.id_grupo " +
                     "JOIN lineas_investigacion l ON l.id_linea = p.id_linea " +
                     "JOIN usuarios u ON u.id_usuario = p.id_responsable " +
                     "LEFT JOIN convocatorias c ON c.id_convocatoria = p.id_convocatoria " +
                     qb.where;
        Long count = jdbc.queryForObject(sql, Long.class, qb.params.toArray());
        return count != null ? count : 0L;
    }

    private QueryBuilder buildProjectsWhere(ReportFilter f) {
        QueryBuilder qb = new QueryBuilder();
        if (f.getGroupId() != null)       qb.and("p.id_grupo = ?",           f.getGroupId());
        if (f.getStatus() != null)         qb.and("p.estado_proyecto = ?",    f.getStatus());
        if (f.getFromDate() != null)       qb.and("p.fecha_creacion >= ?",    f.getFromDate());
        if (f.getToDate() != null)         qb.and("p.fecha_creacion <= ?",    f.getToDate());
        if (f.getResearcherId() != null)   qb.and("p.id_responsable = ?",     f.getResearcherId());
        if (f.getCallId() != null)         qb.and("p.id_convocatoria = ?",    f.getCallId());
        return qb;
    }

    private static final RowMapper<ProjectReport> MAPPER_PROJECT = (rs, rn) -> {
        ProjectReport r = new ProjectReport();
        r.setProjectId(rs.getInt("id_proyecto"));
        r.setProjectCode(rs.getString("codigo_proyecto"));
        r.setProjectTitle(rs.getString("titulo_proyecto"));
        r.setProjectStatus(rs.getString("estado_proyecto"));
        r.setGroupName(rs.getString(COL_GROUP_NAME));
        r.setLineName(rs.getString("nombre_linea"));
        r.setResponsibleName(rs.getString("nombre_responsable"));
        r.setCallTitle(rs.getString("titulo_convocatoria"));
        r.setBudget(rs.getBigDecimal("presupuesto"));
        r.setStartDate(rs.getObject("fecha_inicio", LocalDate.class));
        r.setEndDate(rs.getObject("fecha_fin", LocalDate.class));
        r.setCreatedAt(rs.getObject("fecha_creacion", LocalDateTime.class));
        return r;
    };

    // =========================================================================
    // PROCEDURE REPORTS
    // =========================================================================

    private static final String SQL_PROCEDURES_BASE =
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
    @SuppressWarnings("java:S2077") // Dynamic SQL safe: values passed as positional parameters to JdbcTemplate
    public List<ProcedureReport> findProcedures(ReportFilter f) {
        QueryBuilder qb = buildProceduresWhere(f);
        String sql = SQL_PROCEDURES_BASE + qb.where +
                     " ORDER BY t.fecha_envio DESC" +
                     " LIMIT ? OFFSET ?";
        qb.params.add(f.getSize());
        qb.params.add(f.getOffset());
        return jdbc.query(sql, MAPPER_PROCEDURE, qb.params.toArray());
    }

    @Override
    @SuppressWarnings("java:S2077") // Dynamic SQL safe: values passed as positional parameters to JdbcTemplate
    public long countProcedures(ReportFilter f) {
        QueryBuilder qb = buildProceduresWhere(f);
        String sql = "SELECT COUNT(*) FROM tramites t " +
                     "JOIN usuarios u ON u.id_usuario = t.id_solicitante " +
                     "JOIN grupos_investigacion g ON g.id_grupo = t.id_grupo " +
                     qb.where;
        Long count = jdbc.queryForObject(sql, Long.class, qb.params.toArray());
        return count != null ? count : 0L;
    }

    private QueryBuilder buildProceduresWhere(ReportFilter f) {
        QueryBuilder qb = new QueryBuilder();
        if (f.getGroupId() != null)       qb.and("t.id_grupo = ?",           f.getGroupId());
        if (f.getStatus() != null)         qb.and("t.estado_actual = ?",      f.getStatus());
        if (f.getFromDate() != null)       qb.and("t.fecha_envio >= ?",       f.getFromDate());
        if (f.getToDate() != null)         qb.and("t.fecha_envio <= ?",       f.getToDate());
        if (f.getResearcherId() != null)   qb.and("t.id_solicitante = ?",     f.getResearcherId());
        if (f.getProcedureType() != null)  qb.and("t.tipo_tramite = ?",       f.getProcedureType());
        return qb;
    }

    private static final RowMapper<ProcedureReport> MAPPER_PROCEDURE = (rs, rn) -> {
        ProcedureReport r = new ProcedureReport();
        r.setProcedureId(rs.getInt("id_tramite"));
        r.setProcedureCode(rs.getString("codigo_tramite"));
        r.setProcedureType(rs.getString("tipo_tramite"));
        r.setApplicantName(rs.getString("nombre_solicitante"));
        r.setCurrentStatus(rs.getString("estado_actual"));
        r.setCurrentReviewerRole(rs.getString("rol_revisor_actual"));
        r.setGroupName(rs.getString(COL_GROUP_NAME));
        r.setSubmittedAt(rs.getObject("fecha_envio", LocalDateTime.class));
        r.setUpdatedAt(rs.getObject("fecha_actualizacion", LocalDateTime.class));
        return r;
    };

    // =========================================================================
    // RESOLUTION REPORTS
    // =========================================================================

    private static final String SQL_RESOLUTIONS_BASE =
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
    @SuppressWarnings("java:S2077") // Dynamic SQL safe: values passed as positional parameters to JdbcTemplate
    public List<ResolutionReport> findResolutions(ReportFilter f) {
        QueryBuilder qb = buildResolutionsWhere(f);
        String sql = SQL_RESOLUTIONS_BASE + qb.where +
                     " ORDER BY r.fecha_emision DESC" +
                     " LIMIT ? OFFSET ?";
        qb.params.add(f.getSize());
        qb.params.add(f.getOffset());
        return jdbc.query(sql, MAPPER_RESOLUTION, qb.params.toArray());
    }

    @Override
    @SuppressWarnings("java:S2077") // Dynamic SQL safe: values passed as positional parameters to JdbcTemplate
    public long countResolutions(ReportFilter f) {
        QueryBuilder qb = buildResolutionsWhere(f);
        String sql = "SELECT COUNT(*) FROM resoluciones r " +
                     "JOIN tramites t ON t.id_tramite = r.id_tramite " +
                     "JOIN usuarios u ON u.id_usuario = t.id_solicitante " +
                     qb.where;
        Long count = jdbc.queryForObject(sql, Long.class, qb.params.toArray());
        return count != null ? count : 0L;
    }

    private QueryBuilder buildResolutionsWhere(ReportFilter f) {
        QueryBuilder qb = new QueryBuilder();
        if (f.getFromDate() != null)       qb.and("r.fecha_emision >= ?",     f.getFromDate());
        if (f.getToDate() != null)         qb.and("r.fecha_emision <= ?",     f.getToDate());
        if (f.getResearcherId() != null)   qb.and("t.id_solicitante = ?",     f.getResearcherId());
        if (f.getProcedureType() != null)  qb.and("t.tipo_tramite = ?",       f.getProcedureType());
        return qb;
    }

    private static final RowMapper<ResolutionReport> MAPPER_RESOLUTION = (rs, rn) -> {
        ResolutionReport r = new ResolutionReport();
        r.setResolutionId(rs.getInt("id_resolucion"));
        r.setResolutionNumber(rs.getString("numero_resolucion"));
        r.setIssueDate(rs.getObject("fecha_emision", LocalDate.class));
        r.setSubject(rs.getString("asunto"));
        r.setProcedureCode(rs.getString("codigo_tramite"));
        r.setProcedureType(rs.getString("tipo_tramite"));
        r.setApplicantName(rs.getString("nombre_solicitante"));
        r.setRegisteredAt(rs.getObject("fecha_registro", LocalDateTime.class));
        return r;
    };

    // =========================================================================
    // PROGRESS REPORTS
    // =========================================================================

    private static final String SQL_PROGRESS_REPORTS_BASE =
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
    @SuppressWarnings("java:S2077") // Dynamic SQL safe: values passed as positional parameters to JdbcTemplate
    public List<ProgressReport> findProgressReports(ReportFilter f) {
        QueryBuilder qb = buildProgressReportsWhere(f);
        String sql = SQL_PROGRESS_REPORTS_BASE + qb.where +
                     " ORDER BY ia.fecha_registro DESC" +
                     " LIMIT ? OFFSET ?";
        qb.params.add(f.getSize());
        qb.params.add(f.getOffset());
        return jdbc.query(sql, MAPPER_PROGRESS_REPORT, qb.params.toArray());
    }

    @Override
    @SuppressWarnings("java:S2077") // Dynamic SQL safe: values passed as positional parameters to JdbcTemplate
    public long countProgressReports(ReportFilter f) {
        QueryBuilder qb = buildProgressReportsWhere(f);
        String sql = "SELECT COUNT(*) FROM informes_avance ia " +
                     "JOIN proyectos p ON p.id_proyecto = ia.id_proyecto " +
                     "JOIN grupos_investigacion g ON g.id_grupo = p.id_grupo " +
                     qb.where;
        Long count = jdbc.queryForObject(sql, Long.class, qb.params.toArray());
        return count != null ? count : 0L;
    }

    private QueryBuilder buildProgressReportsWhere(ReportFilter f) {
        QueryBuilder qb = new QueryBuilder();
        if (f.getGroupId() != null)       qb.and("p.id_grupo = ?",            f.getGroupId());
        if (f.getStatus() != null)         qb.and("ia.estado_informe = ?",     f.getStatus());
        if (f.getFromDate() != null)       qb.and("ia.fecha_registro >= ?",    f.getFromDate());
        if (f.getToDate() != null)         qb.and("ia.fecha_registro <= ?",    f.getToDate());
        if (f.getProcedureType() != null)  qb.and("ia.tipo_informe = ?",      f.getProcedureType());
        return qb;
    }

    private static final RowMapper<ProgressReport> MAPPER_PROGRESS_REPORT = (rs, rn) -> {
        ProgressReport r = new ProgressReport();
        r.setReportId(rs.getInt("id_informe"));
        r.setProjectCode(rs.getString("codigo_proyecto"));
        r.setProjectTitle(rs.getString("titulo_proyecto"));
        r.setReportType(rs.getString("tipo_informe"));
        r.setPeriod(rs.getString("periodo"));
        r.setProgressPercentage(rs.getBigDecimal("porcentaje_avance"));
        r.setReportStatus(rs.getString("estado_informe"));
        r.setGroupName(rs.getString(COL_GROUP_NAME));
        r.setRegisteredAt(rs.getObject("fecha_registro", LocalDateTime.class));
        return r;
    };

    // =========================================================================
    // Internal Helper: Dynamic WHERE clauses builder
    // =========================================================================

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
