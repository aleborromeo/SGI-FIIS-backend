package com.sgi.fiis.resolutions.domain.port.out;

public interface DocumentStoragePort {
    Long saveDocument(byte[] fileBytes, String fileName, String contentType);
}
