package com.sgi.fiis.documentacion.application.usecase;

import java.io.InputStream;

public class DocumentDownloadResult {
    private final InputStream inputStream;
    private final String originalName;

    public DocumentDownloadResult(InputStream inputStream, String originalName) {
        this.inputStream = inputStream;
        this.originalName = originalName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getOriginalName() {
        return originalName;
    }
}
