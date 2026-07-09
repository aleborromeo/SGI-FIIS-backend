package com.sgi.fiis.shared.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Integer id;

    @Column(name = "nombre_original", nullable = false, length = 255)
    private String originalName;

    @Column(name = "ruta_almacenamiento", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "tamano_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "tipo_extension", nullable = false, length = 10)
    private String fileExtension;

    @Column(name = "id_usuario_subio", nullable = false)
    private Long creatorId;

    @Column(name = "fecha_carga", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "es_activo", nullable = false)
    @Builder.Default
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
}
