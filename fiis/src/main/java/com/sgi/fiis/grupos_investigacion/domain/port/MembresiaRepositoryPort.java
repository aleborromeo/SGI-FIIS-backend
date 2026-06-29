package com.sgi.fiis.grupos_investigacion.domain.port;

import com.sgi.fiis.grupos_investigacion.domain.model.Membresia;

import java.util.List;
import java.util.Optional;

public interface MembresiaRepositoryPort {
    Membresia save(Membresia membresia);
    Optional<Membresia> findById(Integer id);
    Optional<Membresia> findActivaByUsuarioEnGrupo(Integer idUsuario, Integer idGrupo);
    List<Membresia> findActivasByGrupo(Integer idGrupo);
    boolean existsActivaByUsuario(Integer idUsuario);
}
