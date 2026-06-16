package com.sgi.fiis.resoluciones.domain.port.out;

public interface DocumentoStoragePort {
    Long guardarDocumento(byte[] archivoBytes, String nombreArchivo, String tipoContenido);
}
