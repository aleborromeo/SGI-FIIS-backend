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
    private Long uploadedById;
    private LocalDateTime uploadDate;
    private boolean active;
    private Long proyectoId;
    private Long tramiteId;
    private Long planTesisId;
    private Long informeId;
    private boolean esSubsanacion;

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
        this.proyectoId = builder.proyectoId;
        this.tramiteId = builder.tramiteId;
        this.planTesisId = builder.planTesisId;
        this.informeId = builder.informeId;
        this.esSubsanacion = builder.esSubsanacion;
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
    public Long getUploadedById() { return uploadedById; }
    public void setUploadedById(Long uploadedById) { this.uploadedById = uploadedById; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }
    public Long getTramiteId() { return tramiteId; }
    public void setTramiteId(Long tramiteId) { this.tramiteId = tramiteId; }
    public Long getPlanTesisId() { return planTesisId; }
    public void setPlanTesisId(Long planTesisId) { this.planTesisId = planTesisId; }
    public Long getInformeId() { return informeId; }
    public void setInformeId(Long informeId) { this.informeId = informeId; }
    public boolean isEsSubsanacion() { return esSubsanacion; }
    public void setEsSubsanacion(boolean esSubsanacion) { this.esSubsanacion = esSubsanacion; }

    // Clase Builder Estática sin errores de duplicación
    public static class Builder {
        private Long id;
        private String originalName;
        private String storagePath;
        private String extension;
        private Long sizeBytes;
        private Long uploadedById;
        private LocalDateTime uploadDate;
        private boolean active = true;
        private Long proyectoId;
        private Long tramiteId;
        private Long planTesisId;
        private Long informeId;
        private boolean esSubsanacion = false;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder originalName(String originalName) { this.originalName = originalName; return this; }
        public Builder storagePath(String storagePath) { this.storagePath = storagePath; return this; }
        public Builder extension(String extension) { this.extension = extension; return this; }
        public Builder sizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; return this; }
        public Builder uploadedById(Long uploadedById) { this.uploadedById = uploadedById; return this; }
        public Builder uploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder proyectoId(Long proyectoId) { this.proyectoId = proyectoId; return this; }
        public Builder tramiteId(Long tramiteId) { this.tramiteId = tramiteId; return this; }
        public Builder planTesisId(Long planTesisId) { this.planTesisId = planTesisId; return this; }
        public Builder informeId(Long informeId) { this.informeId = informeId; return this; }
        public Builder esSubsanacion(boolean esSubsanacion) { this.esSubsanacion = esSubsanacion; return this; }

        public Document build() {
            return new Document(this);
        }
    }
}