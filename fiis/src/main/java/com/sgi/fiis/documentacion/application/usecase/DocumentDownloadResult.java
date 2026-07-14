package com.sgi.fiis.documentacion.application.usecase;

import java.io.InputStream;

public class DocumentDownloadResult {
    private final InputStream inputStream;
    private final String originalName;
    private final String extension;
    private final Long sizeBytes;

    public DocumentDownloadResult(InputStream inputStream, String originalName, String extension, Long sizeBytes) {
        this.inputStream = inputStream;
        this.originalName = originalName;
        this.extension = extension;
        this.sizeBytes = sizeBytes;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getExtension() {
        return extension;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }
}
