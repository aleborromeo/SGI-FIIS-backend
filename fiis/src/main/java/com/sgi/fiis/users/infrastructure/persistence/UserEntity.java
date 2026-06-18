package com.sgi.fiis.users.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity mapped to the 'usuarios' table.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "dni", nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "nombres", nullable = false, length = 100)
    private String firstNames;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String lastNames;

    @Column(name = "correo_institucional", nullable = false, unique = true, length = 150)
    private String institutionalEmail;

    @Column(name = "telefono", length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "es_activo", nullable = false)
    private boolean active;

    @Column(name="must_change_password",nullable=false)
    private boolean mustChangePassword;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol_principal", nullable = false)
    private RoleEntity role;

    @Column(name="oauth_provider",length=50)
    private String oauthProvider;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime updatedAt;
}
