package com.sgi.fiis.shared.infrastructure.persistence;

import com.sgi.fiis.shared.application.dto.AuditLogEntryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AuditLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<AuditLogEntryDTO> findAll(int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
            "SELECT a.id_auditoria, a.tabla_afectada, a.id_registro, a.accion, " +
            "a.id_usuario, u.nombres || ' ' || u.apellidos AS nombre_usuario, " +
            "a.datos_anteriores, a.datos_nuevos, a.ip_origen, a.fecha_accion " +
            "FROM auditoria_general a " +
            "LEFT JOIN usuarios u ON a.id_usuario = u.id_usuario " +
            "ORDER BY a.fecha_accion DESC " +
            "LIMIT ? OFFSET ?",
            (rs, rowNum) -> AuditLogEntryDTO.builder()
                .id(rs.getLong("id_auditoria"))
                .tablaAfectada(rs.getString("tabla_afectada"))
                .idRegistro(rs.getLong("id_registro"))
                .accion(rs.getString("accion"))
                .idUsuario(rs.getLong("id_usuario"))
                .nombreUsuario(rs.getString("nombre_usuario"))
                .datosAnteriores(rs.getString("datos_anteriores"))
                .datosNuevos(rs.getString("datos_nuevos"))
                .ipOrigen(rs.getString("ip_origen"))
                .fechaAccion(rs.getTimestamp("fecha_accion") != null
                    ? rs.getTimestamp("fecha_accion").toLocalDateTime() : null)
                .build(),
            size, offset
        );
    }

    public List<AuditLogEntryDTO> findByTabla(String tabla, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
            "SELECT a.id_auditoria, a.tabla_afectada, a.id_registro, a.accion, " +
            "a.id_usuario, u.nombres || ' ' || u.apellidos AS nombre_usuario, " +
            "a.datos_anteriores, a.datos_nuevos, a.ip_origen, a.fecha_accion " +
            "FROM auditoria_general a " +
            "LEFT JOIN usuarios u ON a.id_usuario = u.id_usuario " +
            "WHERE a.tabla_afectada = ? " +
            "ORDER BY a.fecha_accion DESC " +
            "LIMIT ? OFFSET ?",
            (rs, rowNum) -> AuditLogEntryDTO.builder()
                .id(rs.getLong("id_auditoria"))
                .tablaAfectada(rs.getString("tabla_afectada"))
                .idRegistro(rs.getLong("id_registro"))
                .accion(rs.getString("accion"))
                .idUsuario(rs.getLong("id_usuario"))
                .nombreUsuario(rs.getString("nombre_usuario"))
                .datosAnteriores(rs.getString("datos_anteriores"))
                .datosNuevos(rs.getString("datos_nuevos"))
                .ipOrigen(rs.getString("ip_origen"))
                .fechaAccion(rs.getTimestamp("fecha_accion") != null
                    ? rs.getTimestamp("fecha_accion").toLocalDateTime() : null)
                .build(),
            tabla, size, offset
        );
    }

    public List<AuditLogEntryDTO> findByIdRegistro(Long idRegistro, int page, int size) {
        int offset = page * size;
        return jdbcTemplate.query(
            "SELECT a.id_auditoria, a.tabla_afectada, a.id_registro, a.accion, " +
            "a.id_usuario, u.nombres || ' ' || u.apellidos AS nombre_usuario, " +
            "a.datos_anteriores, a.datos_nuevos, a.ip_origen, a.fecha_accion " +
            "FROM auditoria_general a " +
            "LEFT JOIN usuarios u ON a.id_usuario = u.id_usuario " +
            "WHERE a.id_registro = ? " +
            "ORDER BY a.fecha_accion DESC " +
            "LIMIT ? OFFSET ?",
            (rs, rowNum) -> AuditLogEntryDTO.builder()
                .id(rs.getLong("id_auditoria"))
                .tablaAfectada(rs.getString("tabla_afectada"))
                .idRegistro(rs.getLong("id_registro"))
                .accion(rs.getString("accion"))
                .idUsuario(rs.getLong("id_usuario"))
                .nombreUsuario(rs.getString("nombre_usuario"))
                .datosAnteriores(rs.getString("datos_anteriores"))
                .datosNuevos(rs.getString("datos_nuevos"))
                .ipOrigen(rs.getString("ip_origen"))
                .fechaAccion(rs.getTimestamp("fecha_accion") != null
                    ? rs.getTimestamp("fecha_accion").toLocalDateTime() : null)
                .build(),
            idRegistro, size, offset
        );
    }
}
