package com.sgi.fiis.grupos_investigacion.domain.port;

import com.sgi.fiis.grupos_investigacion.domain.model.GrupoInvestigacion;

import java.util.List;
import java.util.Optional;

public interface GrupoInvestigacionRepositoryPort {
    GrupoInvestigacion save(GrupoInvestigacion grupo);
    Optional<GrupoInvestigacion> findById(Integer id);
    List<GrupoInvestigacion> findAll();
    boolean existsByCodigo(String codigoGrupo);
    boolean existeUsuarioActivo(Integer idUsuario);
}
