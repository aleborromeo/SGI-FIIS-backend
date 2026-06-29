package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "membresias_grupo")
@Getter
@Setter
public class GroupMembershipEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_grupo", nullable = false)
    private ResearchGroupEntity group;

    @Column(name = "id_usuario", nullable = false)
    private Long userId;

    @Column(name = "es_activo", nullable = false)
    private Boolean active;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "fecha_fin")
    private LocalDateTime endDate;
}
