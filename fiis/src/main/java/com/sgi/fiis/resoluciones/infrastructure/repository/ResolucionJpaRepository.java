package com.sgi.fiis.resoluciones.infrastructure.repository;

import com.sgi.fiis.resoluciones.infrastructure.entity.ResolucionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResolucionJpaRepository extends JpaRepository<ResolucionEntity, Long> {
    boolean existsByNumeroResolucion(String numeroResolucion);
}
