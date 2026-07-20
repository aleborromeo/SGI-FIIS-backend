package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA entity mapped to the informes_avance table.
 * Infrastructure Layer -- DB annotations live here, far from the domain.
 */
@Entity
@Table(name = "informes_avance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_informe")
    private Long id;

    @Column(name = "id_proyecto", nullable = false)
    private Long projectId;

    @Column(name = "tipo_informe", nullable = false, length = 50)
    private String reportType;

    @Column(name = "periodo", nullable = false, length = 50)
    private String period;

    @Column(name = "porcentaje_avance", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPercentage;

    @Column(name = "logros", nullable = false, columnDefinition = "TEXT")
    private String achievements;

    @Column(name = "dificultades", nullable = false, columnDefinition = "TEXT")
    private String difficulties;

    @Column(name = "recomendaciones", nullable = false, columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "id_documento_adjunto")
    private Long attachedDocumentId;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observation;

    @Column(name = "estado_informe", nullable = false, length = 30)
    private String reportStatus;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime lastUpdatedDate;
}
