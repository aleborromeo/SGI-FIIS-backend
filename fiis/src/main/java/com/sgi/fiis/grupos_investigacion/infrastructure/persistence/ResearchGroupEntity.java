package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "grupos_investigacion")
@Getter
@Setter
public class ResearchGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Integer id;

    @Column(name = "codigo_grupo", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "nombre_grupo", nullable = false, length = 150)
    private String name;

    @Column(name = "es_activo", nullable = false)
    private boolean active;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coordinador_actual")
    private UserEntity currentCoordinator;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now(ZoneId.of("UTC"));
        }
    }
}
