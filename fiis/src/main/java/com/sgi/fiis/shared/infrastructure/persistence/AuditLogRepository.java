package com.sgi.fiis.shared.infrastructure.persistence;

import com.sgi.fiis.shared.application.dto.AuditLogEntryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuditLogRepository {

    private static final String COL_ID_AUDITORIA = "id_auditoria";
    private static final String COL_TABLA_AFECTADA = "tabla_afectada";
    private static final String COL_ID_REGISTRO = "id_registro";
    private static final String COL_ACCION = "accion";
    private static final String COL_ID_USUARIO = "id_usuario";
    private static final String COL_NOMBRE_USUARIO = "nombre_usuario";
    private static final String COL_DATOS_ANTERIORES = "datos_anteriores";
    private static final String COL_DATOS_NUEVOS = "datos_nuevos";
    private static final String COL_IP_ORIGEN = "ip_origen";
    private static final String COL_FECHA_ACCION = "fecha_accion";

    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        Long totalRecords = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM auditoria_general", Long.class);
        Long tablesCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT tabla_afectada) FROM auditoria_general", Long.class);
        Long usersCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT id_usuario) FROM auditoria_general", Long.class);
        Long todayCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM auditoria_general WHERE DATE(fecha_accion) = CURRENT_DATE", Long.class);

        stats.put("totalRecords", totalRecords != null ? totalRecords : 0);
        stats.put("tablesAffected", tablesCount != null ? tablesCount : 0);
        stats.put("activeUsers", usersCount != null ? usersCount : 0);
        stats.put("todayActions", todayCount != null ? todayCount : 0);

        return stats;
    }

    public List<AuditLogEntryDTO> findAll(int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
            "SELECT a." + COL_ID_AUDITORIA + ", a." + COL_TABLA_AFECTADA + ", a." + COL_ID_REGISTRO + ", a." + COL_ACCION + ", " +
            "a." + COL_ID_USUARIO + ", u.nombres || ' ' || u.apellidos AS " + COL_NOMBRE_USUARIO + ", " +
            "a." + COL_DATOS_ANTERIORES + ", a." + COL_DATOS_NUEVOS + ", a." + COL_IP_ORIGEN + ", a." + COL_FECHA_ACCION + " " +
            "FROM auditoria_general a " +
            "LEFT JOIN usuarios u ON a." + COL_ID_USUARIO + " = u." + COL_ID_USUARIO + " " +
            "ORDER BY a." + COL_FECHA_ACCION + " DESC " +
            "LIMIT ? OFFSET ?",
            (rs, rowNum) -> AuditLogEntryDTO.builder()
                .id(rs.getLong(COL_ID_AUDITORIA))
                .tablaAfectada(rs.getString(COL_TABLA_AFECTADA))
                .idRegistro(rs.getLong(COL_ID_REGISTRO))
                .accion(rs.getString(COL_ACCION))
                .idUsuario(rs.getLong(COL_ID_USUARIO))
                .nombreUsuario(rs.getString(COL_NOMBRE_USUARIO))
                .datosAnteriores(rs.getString(COL_DATOS_ANTERIORES))
                .datosNuevos(rs.getString(COL_DATOS_NUEVOS))
                .ipOrigen(rs.getString(COL_IP_ORIGEN))
                .fechaAccion(rs.getTimestamp(COL_FECHA_ACCION) != null
                    ? rs.getTimestamp(COL_FECHA_ACCION).toLocalDateTime() : null)
                .build(),
            size, offset
        );
    }

    public List<AuditLogEntryDTO> findByTabla(String tabla, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
            "SELECT a." + COL_ID_AUDITORIA + ", a." + COL_TABLA_AFECTADA + ", a." + COL_ID_REGISTRO + ", a." + COL_ACCION + ", " +
            "a." + COL_ID_USUARIO + ", u.nombres || ' ' || u.apellidos AS " + COL_NOMBRE_USUARIO + ", " +
            "a." + COL_DATOS_ANTERIORES + ", a." + COL_DATOS_NUEVOS + ", a." + COL_IP_ORIGEN + ", a." + COL_FECHA_ACCION + " " +
            "FROM auditoria_general a " +
            "LEFT JOIN usuarios u ON a." + COL_ID_USUARIO + " = u." + COL_ID_USUARIO + " " +
            "WHERE a." + COL_TABLA_AFECTADA + " = ? " +
            "ORDER BY a." + COL_FECHA_ACCION + " DESC " +
            "LIMIT ? OFFSET ?",
            (rs, rowNum) -> AuditLogEntryDTO.builder()
                .id(rs.getLong(COL_ID_AUDITORIA))
                .tablaAfectada(rs.getString(COL_TABLA_AFECTADA))
                .idRegistro(rs.getLong(COL_ID_REGISTRO))
                .accion(rs.getString(COL_ACCION))
                .idUsuario(rs.getLong(COL_ID_USUARIO))
                .nombreUsuario(rs.getString(COL_NOMBRE_USUARIO))
                .datosAnteriores(rs.getString(COL_DATOS_ANTERIORES))
                .datosNuevos(rs.getString(COL_DATOS_NUEVOS))
                .ipOrigen(rs.getString(COL_IP_ORIGEN))
                .fechaAccion(rs.getTimestamp(COL_FECHA_ACCION) != null
                    ? rs.getTimestamp(COL_FECHA_ACCION).toLocalDateTime() : null)
                .build(),
            tabla, size, offset
        );
    }

    public List<AuditLogEntryDTO> findByIdRegistro(Long idRegistro, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
            "SELECT a." + COL_ID_AUDITORIA + ", a." + COL_TABLA_AFECTADA + ", a." + COL_ID_REGISTRO + ", a." + COL_ACCION + ", " +
            "a." + COL_ID_USUARIO + ", u.nombres || ' ' || u.apellidos AS " + COL_NOMBRE_USUARIO + ", " +
            "a." + COL_DATOS_ANTERIORES + ", a." + COL_DATOS_NUEVOS + ", a." + COL_IP_ORIGEN + ", a." + COL_FECHA_ACCION + " " +
            "FROM auditoria_general a " +
            "LEFT JOIN usuarios u ON a." + COL_ID_USUARIO + " = u." + COL_ID_USUARIO + " " +
            "WHERE a." + COL_ID_REGISTRO + " = ? " +
            "ORDER BY a." + COL_FECHA_ACCION + " DESC " +
            "LIMIT ? OFFSET ?",
            (rs, rowNum) -> AuditLogEntryDTO.builder()
                .id(rs.getLong(COL_ID_AUDITORIA))
                .tablaAfectada(rs.getString(COL_TABLA_AFECTADA))
                .idRegistro(rs.getLong(COL_ID_REGISTRO))
                .accion(rs.getString(COL_ACCION))
                .idUsuario(rs.getLong(COL_ID_USUARIO))
                .nombreUsuario(rs.getString(COL_NOMBRE_USUARIO))
                .datosAnteriores(rs.getString(COL_DATOS_ANTERIORES))
                .datosNuevos(rs.getString(COL_DATOS_NUEVOS))
                .ipOrigen(rs.getString(COL_IP_ORIGEN))
                .fechaAccion(rs.getTimestamp(COL_FECHA_ACCION) != null
                    ? rs.getTimestamp(COL_FECHA_ACCION).toLocalDateTime() : null)
                .build(),
            idRegistro, size, offset
        );
    }
}
