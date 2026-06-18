package pe.unas.fiis.sgifiis.thesis.infrastructure.external;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import pe.unas.fiis.sgifiis.thesis.domain.port.out.GrupoInvestigacionValidationPort;

@Component
public class GrupoInvestigacionValidationJdbcAdapter implements GrupoInvestigacionValidationPort {
    private final JdbcTemplate jdbcTemplate;
    public GrupoInvestigacionValidationJdbcAdapter(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }
    public boolean existeGrupoActivo(Integer idGrupo) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM grupos_investigacion WHERE id_grupo = ? AND es_activo = TRUE", Integer.class, idGrupo);
        return count != null && count > 0;
    }
    public boolean existeLineaActiva(Integer idLinea) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM lineas_investigacion WHERE id_linea = ? AND es_activa = TRUE", Integer.class, idLinea);
        return count != null && count > 0;
    }
    public boolean lineaPerteneceAlGrupo(Integer idGrupo, Integer idLinea) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM lineas_por_grupo WHERE id_grupo = ? AND id_linea = ?", Integer.class, idGrupo, idLinea);
        return count != null && count > 0;
    }
}
