package com.sgi.fiis.thesis.domain.port.out;

public interface DocumentValidationPort {
    boolean existeDocumentoActivo(Integer idDocumento);
    boolean documentoPerteneceAUsuario(Integer idDocumento, Long idUsuario);
}
