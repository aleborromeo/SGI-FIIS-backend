package com.sgi.fiis.users.domain.port;

import com.sgi.fiis.users.domain.model.Rol;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio para operaciones de persistencia de Roles.
 */
public interface RolRepositoryPort {
    Optional<Rol> findByCodigo(String codigoRol);
    List<Rol> findAll();
}
