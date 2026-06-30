package com.sgi.fiis.resoluciones.infrastructure.adapter;

import com.sgi.fiis.resoluciones.domain.port.out.TramiteRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TramiteRepositoryAdapter implements TramiteRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public TramiteRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existeTramite(Long idTramite) {
        String sql = "SELECT count(*) FROM tramites WHERE id_tramite = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idTramite);
        return count != null && count > 0;
    }

    @Override
    public void actualizarEstadoAAprobadoConResolucion(Long idTramite) {
        String sql = "UPDATE tramites SET estado_actual = 'APROBADO_CON_RESOLUCION' WHERE id_tramite = ?";
        jdbcTemplate.update(sql, idTramite);
    }
}
