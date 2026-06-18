package com.sgi.fiis.documentacion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Long id;

    @Column(name = "nombre_original", nullable = false)
    private String originalName;

    @Column(name = "ruta_almacenamiento", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "tipo_extension", nullable = false, length = 10)
    private String extension;

    @Column(name = "tamano_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "id_usuario_subio", nullable = false)
    private Long uploadedById;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime uploadDate;

    @Column(name = "es_activo", nullable = false)
    private boolean active;

    /**
     * Constructor por defecto requerido por la especificación de JPA 
     * para la instanciación de entidades mediante reflexión.
     */
    public DocumentEntity() {
        // No se requiere inicialización de campos para el motor de persistencia
    }

    // Getters y Setters estándar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }
    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public Long getUploadedById() { return uploadedById; }
    public void setUploadedById(Long uploadedById) { this.uploadedById = uploadedById; }
    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}