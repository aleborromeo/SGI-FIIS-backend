package com.sgi.fiis.users.domain.port;

import com.sgi.fiis.users.domain.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Domain port for Role persistence operations.
 */
public interface RoleRepositoryPort {
    Optional<Role> findByCode(String roleCode);
    List<Role> findAll();
}
