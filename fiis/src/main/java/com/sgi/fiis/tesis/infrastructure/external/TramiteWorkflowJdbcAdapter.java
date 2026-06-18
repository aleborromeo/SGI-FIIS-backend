package pe.unas.fiis.sgifiis.thesis.infrastructure.external;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Year;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import pe.unas.fiis.sgifiis.thesis.domain.EstadoTramiteTesis;
import pe.unas.fiis.sgifiis.thesis.domain.RolRevisor;
import pe.unas.fiis.sgifiis.thesis.domain.port.out.TramiteWorkflowPort;

@Component
public class TramiteWorkflowJdbcAdapter implements TramiteWorkflowPort {
    private final JdbcTemplate jdbcTemplate;
    public TramiteWorkflowJdbcAdapter(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    @Override
    public Integer crearTramitePlanTesis(Integer idPlanTesis, Integer idSolicitante, Integer idGrupo) {
        String codigo = generarCodigoTramite();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO tramites (codigo_tramite, tipo_tramite, id_solicitante, id_grupo, estado_actual,
                                      rol_revisor_actual, id_referencia_tesis)
                VALUES (?, 'PLAN_TESIS', ?, ?, ?, ?, ?)
                """, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, codigo);
            ps.setInt(2, idSolicitante);
            ps.setInt(3, idGrupo);
            ps.setString(4, EstadoTramiteTesis.PENDIENTE_COORDINADOR.name());
            ps.setString(5, RolRevisor.COORDINADOR_GRUPO.name());
            ps.setInt(6, idPlanTesis);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        Integer idTramite = key == null ? obtenerIdTramitePorPlanTesis(idPlanTesis) : key.intValue();
        registrarMovimiento(idTramite, idSolicitante, "REGISTRAR_PLAN_TESIS", "REGISTRADO",
                EstadoTramiteTesis.PENDIENTE_COORDINADOR.name(), "Trámite generado automáticamente para plan de tesis", null);
        return idTramite;
    }

    @Override
    public void derivarPlanTesis(Integer idPlanTesis, Integer idUsuarioAccion, EstadoTramiteTesis estadoNuevo,
                                 RolRevisor rolNuevo, String accion, String observacion, Integer idDocumentoAdjunto) {
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

    private void registrarMovimiento(Integer idTramite, Integer idUsuarioAccion, String accion, String estadoAnterior,
                                     String estadoNuevo, String observacion, Integer idDocumentoAdjunto) {
        jdbcTemplate.update("""
            INSERT INTO movimientos_tramite (id_tramite, id_usuario_accion, accion, estado_anterior, estado_nuevo,
                                             observacion, id_documento_adjunto)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """, idTramite, idUsuarioAccion, accion, estadoAnterior, estadoNuevo, observacion, idDocumentoAdjunto);
    }

    private String generarCodigoTramite() {
        Integer siguiente = jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id_tramite), 0) + 1 FROM tramites", Integer.class);
        return "TES-" + Year.now().getValue() + "-" + String.format("%05d", siguiente == null ? 1 : siguiente);
    }
}
