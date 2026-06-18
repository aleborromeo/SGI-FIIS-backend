package pe.unas.fiis.sgifiis.thesis.domain.port.out;

public interface DocumentoValidationPort {
    boolean existeDocumentoActivo(Integer idDocumento);
    boolean documentoPerteneceAUsuario(Integer idDocumento, Integer idUsuario);
}
