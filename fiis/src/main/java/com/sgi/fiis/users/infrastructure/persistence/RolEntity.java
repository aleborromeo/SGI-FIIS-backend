package com.sgi.fiis.users.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad JPA mapeada a la tabla 'roles'.
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long id;

    @Column(name = "codigo_rol", nullable = false, unique = true, length = 50)
    private String codigoRol;

    @Column(length = 255)
    private String descripcion;
}
