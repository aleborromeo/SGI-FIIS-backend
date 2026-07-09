package com.sgi.fiis.proyectos.infrastructure.persistence;

import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "proyectos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Integer id;

    @Column(name = "codigo_proyecto", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "titulo_proyecto", nullable = false, length = 500)
    private String title;

    @Column(name = "resumen", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "objetivo_general", nullable = false, columnDefinition = "TEXT")
    private String generalObjective;

    @Column(name = "titulo_jsonb", columnDefinition = "jsonb", nullable = false)
    private String titleJson;

    @Column(name = "resumen_jsonb", columnDefinition = "jsonb", nullable = false)
    private String summaryJson;

    @Column(name = "objetivo_general_jsonb", columnDefinition = "jsonb", nullable = false)
    private String generalObjectiveJson;

    @Column(name = "lugar_ejecucion_jsonb", columnDefinition = "jsonb", nullable = false)
    private String executionPlaceJson;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_linea", nullable = false)
    private ResearchLineEntity researchLine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_grupo", nullable = false)
    private ResearchGroupEntity group;

    @Column(name = "presupuesto", nullable = false, precision = 12, scale = 2)
    private BigDecimal budget;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate startDate;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate endDate;

    @Column(name = "lugar_ejecucion", nullable = false, length = 255)
    private String executionPlace;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_responsable", nullable = false)
    private UserEntity responsible;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_convocatoria")
    private ResearchCallEntity researchCall;

    @Column(name = "id_documento_actual")
    private Integer documentId;

    @Column(name = "estado", nullable = false, length = 50)
    private String status;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(ZoneId.of("UTC"));
        updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
        if (titleJson == null) { titleJson = "{}"; }
        if (summaryJson == null) { summaryJson = "{}"; }
        if (generalObjectiveJson == null) { generalObjectiveJson = "{}"; }
        if (executionPlaceJson == null) { executionPlaceJson = "{}"; }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
}
