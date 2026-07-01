package com.sgi.fiis.thesis.infrastructure.external;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.sgi.fiis.thesis.domain.port.out.DocumentValidationPort;

@Component
public class DocumentValidationJdbcAdapter implements DocumentValidationPort {
    private final JdbcTemplate jdbcTemplate;
    public DocumentValidationJdbcAdapter(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }
    public boolean existeDocumentoActivo(Integer idDocumento) {
        if (idDocumento == null) return false;
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM documentos WHERE id_documento = ? AND es_activo = TRUE", Integer.class, idDocumento);
        return count != null && count > 0;
    }
    public boolean documentoPerteneceAUsuario(Integer idDocumento, Long idUsuario) {
        if (idDocumento == null || idUsuario == null) return false;
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM documentos WHERE id_documento = ? AND id_usuario_subio = ? AND es_activo = TRUE", Integer.class, idDocumento, idUsuario);
        return count != null && count > 0;
    }
}
