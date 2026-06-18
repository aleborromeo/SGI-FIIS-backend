package com.sgi.fiis.proyectos.infrastructure.persistence;

import com.sgi.fiis.users.infrastructure.persistence.UsuarioEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "miembros_proyecto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_miembro")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_proyecto", nullable = false)
    private ProjectEntity project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioEntity user;

    @Column(name = "rol", nullable = false, length = 30)
    @Builder.Default
    private String role = "INVESTIGADOR";

}
