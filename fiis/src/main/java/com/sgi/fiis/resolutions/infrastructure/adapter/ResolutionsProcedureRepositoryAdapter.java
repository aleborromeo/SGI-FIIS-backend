package com.sgi.fiis.resolutions.infrastructure.adapter;

import com.sgi.fiis.resolutions.domain.port.out.ProcedureRepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("resolutionsProcedureRepositoryAdapter")
public class ResolutionsProcedureRepositoryAdapter implements ProcedureRepositoryPort {

    private final JdbcTemplate jdbcTemplate;

    public ResolutionsProcedureRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean existsProcedure(Long idTramite) {
        String sql = "SELECT count(*) FROM tramites WHERE id_tramite = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idTramite);
        return count != null && count > 0;
    }

    @Override
    public void updateStatusToApprovedWithResolution(Long idTramite) {
        String sql = "UPDATE tramites SET estado_actual = 'APROBADO_CON_RESOLUCION' WHERE id_tramite = ?";
        jdbcTemplate.update(sql, idTramite);
    }
}
