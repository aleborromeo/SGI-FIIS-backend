package com.sgi.fiis.users.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA Entity mapped to 'roles' table.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long id;

    @Column(name = "codigo_rol", nullable = false, unique = true, length = 50)
    private String roleCode;

    @Column(name = "descripcion", length = 255)
    private String description;
}
