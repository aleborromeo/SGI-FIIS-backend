package com.sgi.fiis.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataRolRepository extends JpaRepository<RolEntity, Long> {
    Optional<RolEntity> findByCodigoRol(String codigoRol);
}
