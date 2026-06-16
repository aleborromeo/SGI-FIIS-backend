package com.sgi.fiis.documentacion.domain.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Document {
    private Long id;
    private String originalName;
    private String storagePath;
    private String extension;
    private Long sizeBytes;
    private Integer uploadedById;
    private LocalDateTime uploadDate;
    private boolean active;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("PDF", "DOC", "DOCX");

    // Constructor privado para el Builder
    private Document(Builder builder) {
        this.id = builder.id;
        this.originalName = builder.originalName;
        this.storagePath = builder.storagePath;
        setExtension(builder.extension); // Valida la extensión al construir
        this.sizeBytes = builder.sizeBytes;
        this.uploadedById = builder.uploadedById;
        this.uploadDate = builder.uploadDate;
        this.active = builder.active;
    }

    public Document() {}

    public static Builder builder() {
        return new Builder();
    }

    public void setExtension(String extension) {
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toUpperCase())) {
            throw new IllegalArgumentException("Extensión de archivo no permitida. Solo se admite PDF, DOC o DOCX.");
        }
        this.extension = extension.toUpperCase();
    }

    // Soporte de borrado lógico (RN-10)
    public void deactivate() {
        this.active = false;
    }

    // Getters y Setters estándar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public String getExtension() { return extension; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public Integer getUploadedById() { return uploadedById; }
    public void setUploadedById(Integer uploadedById) { this.uploadedById = uploadedById; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    // Clase Builder Estática sin errores de duplicación
    public static class Builder {
        private Long id;
        private String originalName;
        private String storagePath;
        private String extension;
        private Long sizeBytes;
        private Integer uploadedById;
        private LocalDateTime uploadDate;
        private boolean active = true;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder originalName(String originalName) { this.originalName = originalName; return this; }
        public Builder storagePath(String storagePath) { this.storagePath = storagePath; return this; }
        public Builder extension(String extension) { this.extension = extension; return this; }
        public Builder sizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; return this; }
        public Builder uploadedById(Integer uploadedById) { this.uploadedById = uploadedById; return this; }
        public Builder uploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; return this; }
        public Builder active(boolean active) { this.active = active; return this; }

        public Document build() {
            return new Document(this);
        }
    }
}