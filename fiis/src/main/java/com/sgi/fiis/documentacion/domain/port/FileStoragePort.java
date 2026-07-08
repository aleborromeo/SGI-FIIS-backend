package com.sgi.fiis.documentacion.domain.port;

import java.io.InputStream;

public interface FileStoragePort {
    String store(InputStream fileStream, String fileName);
    InputStream load(String storagePath);
}