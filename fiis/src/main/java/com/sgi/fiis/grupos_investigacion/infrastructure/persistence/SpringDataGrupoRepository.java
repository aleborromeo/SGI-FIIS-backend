package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataGrupoRepository extends JpaRepository<GrupoInvestigacionEntity, Integer> {
    boolean existsByCodigoGrupo(String codigoGrupo);
}
