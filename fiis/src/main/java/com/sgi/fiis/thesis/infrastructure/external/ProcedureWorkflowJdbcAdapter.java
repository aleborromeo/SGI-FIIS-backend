package com.sgi.fiis.thesis.infrastructure.external;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import com.sgi.fiis.thesis.domain.ThesisProcedureStatus;
import com.sgi.fiis.thesis.domain.ReviewerRole;
import com.sgi.fiis.thesis.domain.port.out.ProcedureWorkflowPort;

@Component
public class ProcedureWorkflowJdbcAdapter implements ProcedureWorkflowPort {
    private final JdbcTemplate jdbcTemplate;
    public ProcedureWorkflowJdbcAdapter(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public Integer crearTramitePlanTesis(Integer idPlanTesis, Long idSolicitante, Integer idGrupo) {
        String codigo = generarCodigoTramite();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual,
                                      rol_revisor_actual, id_referencia_tesis)
                VALUES (?, 'PLAN_TESIS', ?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, codigo);
            ps.setLong(2, idSolicitante);
            ps.setInt(3, idGrupo);
            ps.setString(4, ThesisProcedureStatus.PENDIENTE_COORDINADOR.name());
            ps.setString(5, ReviewerRole.COORDINADOR_GRUPO.name());
            ps.setInt(6, idPlanTesis);
            return ps;
        }, keyHolder);
        Integer idTramite = null;
        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id_tramite")) {
            idTramite = ((Number) keyHolder.getKeys().get("id_tramite")).intValue();
        } else {
            idTramite = obtenerIdTramitePorPlanTesis(idPlanTesis);
        }
        registrarMovimiento(idTramite, idSolicitante, "REGISTRAR_PLAN_TESIS", "REGISTRADO",
                ThesisProcedureStatus.PENDIENTE_COORDINADOR.name(), "Trámite generado automáticamente para plan de tesis", null);
        return idTramite;
    }

    @Override
    public void derivarPlanTesis(Integer idPlanTesis, Long idUsuarioAccion, ThesisProcedureStatus estadoNuevo,
                                 ReviewerRole rolNuevo, String accion, String observacion, Integer idDocumentoAdjunto) {
        Integer idTramite = obtenerIdTramitePorPlanTesis(idPlanTesis);
        String estadoAnterior = jdbcTemplate.queryForObject("SELECT estado_actual FROM tramites WHERE id_tramite = ?", String.class, idTramite);
        jdbcTemplate.update("""
            UPDATE tramites
               SET estado_actual = ?, rol_revisor_actual = ?, fecha_actualizacion = NOW()
             WHERE id_tramite = ?
            """, estadoNuevo.name(), rolNuevo.name(), idTramite);
        registrarMovimiento(idTramite, idUsuarioAccion, accion, estadoAnterior, estadoNuevo.name(), observacion, idDocumentoAdjunto);
    }

    @Override
    public Integer obtenerIdTramitePorPlanTesis(Integer idPlanTesis) {
        return jdbcTemplate.queryForObject("SELECT id_tramite FROM tramites WHERE tipo_tramite = 'PLAN_TESIS' AND id_referencia_tesis = ?", Integer.class, idPlanTesis);
    }

    @Override
    public String obtenerEstadoTramitePorPlanTesis(Integer idPlanTesis) {
        return jdbcTemplate.queryForObject("SELECT estado_actual FROM tramites WHERE tipo_tramite = 'PLAN_TESIS' AND id_referencia_tesis = ?", String.class, idPlanTesis);
    }

    @Override
    public String obtenerRevisorTramitePorPlanTesis(Integer idPlanTesis) {
        return jdbcTemplate.queryForObject("SELECT rol_revisor_actual FROM tramites WHERE tipo_tramite = 'PLAN_TESIS' AND id_referencia_tesis = ?", String.class, idPlanTesis);
    }

    @Override
    public List<Integer> findPlanTesisIdsByRevisor(ReviewerRole rolRevisor) {
        return jdbcTemplate.queryForList(
                "SELECT id_referencia_tesis FROM tramites WHERE tipo_tramite = 'PLAN_TESIS' AND rol_revisor_actual = ?",
                Integer.class, rolRevisor.name());
    }

    @Override
    public Integer registrarResolucion(Integer idPlanTesis, Long idUsuarioAccion,
                                        String numeroResolucion, LocalDate fechaEmision,
                                        String asunto, Integer idDocumentoAdjunto) {
        Integer idTramite = obtenerIdTramitePorPlanTesis(idPlanTesis);
        String estadoAnterior = jdbcTemplate.queryForObject(
                "SELECT estado_actual FROM tramites WHERE id_tramite = ?", String.class, idTramite);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO resoluciones (numero_resolucion, fecha_emision, asunto, id_tramite, id_documento_adjunto)
                VALUES (?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, numeroResolucion);
            ps.setDate(2, java.sql.Date.valueOf(fechaEmision));
            ps.setString(3, asunto);
            ps.setInt(4, idTramite);
            ps.setInt(5, idDocumentoAdjunto);
            return ps;
        }, keyHolder);
        jdbcTemplate.update("""
            UPDATE tramites
               SET estado_actual = ?, rol_revisor_actual = ?, fecha_actualizacion = NOW()
             WHERE id_tramite = ?
            """, ThesisProcedureStatus.APROBADO_CON_RESOLUCION.name(),
                ReviewerRole.SIN_REVISOR.name(), idTramite);
        registrarMovimiento(idTramite, idUsuarioAccion, "REGISTRAR_RESOLUCION",
                estadoAnterior, ThesisProcedureStatus.APROBADO_CON_RESOLUCION.name(),
                "Resolución " + numeroResolucion + ": " + asunto, idDocumentoAdjunto);
        Integer idResolucion = null;
        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id_resolucion")) {
            idResolucion = ((Number) keyHolder.getKeys().get("id_resolucion")).intValue();
        }
        return idResolucion;
    }

    private void registrarMovimiento(Integer idTramite, Long idUsuarioAccion, String accion, String estadoAnterior,
                                      String estadoNuevo, String observacion, Integer idDocumentoAdjunto) {
        jdbcTemplate.update("""
            INSERT INTO movimientos_tramite (id_tramite, id_usuario_accion, accion, estado_anterior, estado_nuevo,
                                             observacion, id_documento_adjunto)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """, idTramite, idUsuarioAccion, accion, estadoAnterior, estadoNuevo, observacion, idDocumentoAdjunto);
    }

    private String generarCodigoTramite() {
        Integer siguiente = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id_tramite), 0) + 1 FROM tramites", Integer.class);
        return "TES-" + Year.now(java.time.ZoneId.systemDefault()).getValue() + "-" + String.format("%05d", siguiente == null ? 1 : siguiente);
    }
}
