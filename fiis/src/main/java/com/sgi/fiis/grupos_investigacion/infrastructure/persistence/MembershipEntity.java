package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "membresias_grupo")
@Getter
@Setter
public class MembershipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia")
    private Integer id;

    @Column(name = "id_grupo", nullable = false)
    private Integer groupId;

    @Column(name = "id_usuario", nullable = false)
    private Integer userId;

    @Column(name = "es_activo", nullable = false)
    private boolean active;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "fecha_fin")
    private LocalDateTime endDate;
}
