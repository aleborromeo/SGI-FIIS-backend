package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataMembresiaRepository extends JpaRepository<MembresiaEntity, Integer> {
    Optional<MembresiaEntity> findByIdUsuarioAndIdGrupoAndEsActivoTrue(Integer idUsuario, Integer idGrupo);
    List<MembresiaEntity> findByIdGrupoAndEsActivoTrue(Integer idGrupo);
    boolean existsByIdUsuarioAndEsActivoTrue(Integer idUsuario);
}
