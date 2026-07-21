package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "tramites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tramite")
    private Integer id;

    @Column(name = "codigo_tramite", nullable = false, unique = true, length = 30)
    private String code;

    @Column(name = "tipo_tramite", nullable = false, length = 30)
    private String procedureType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitante", nullable = false)
    private UserEntity applicant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grupo")
    private ResearchGroupEntity group;

    @Column(name = "estado_actual", nullable = false, length = 30)
    private String status;

    @Column(name = "rol_revisor_actual", length = 30)
    private String reviewerRole;

    @Column(name = "fecha_envio", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_referencia_proyecto")
    private ProjectEntity projectReference;

    @Column(name = "id_referencia_tesis")
    private Long thesisReferenceId;

    @Column(name = "id_referencia_informe")
    private Long reportReferenceId;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now(ZoneId.of("UTC"));
        updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
}
