package com.sgi.fiis.lineas_investigacion.domain.port;

import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;

import java.util.List;
import java.util.Optional;

public interface LineaInvestigacionRepositoryPort {
    LineaInvestigacion save(LineaInvestigacion linea);
    Optional<LineaInvestigacion> findById(Integer id);
    List<LineaInvestigacion> findAll();
    List<LineaInvestigacion> findAllActivas();
    List<LineaInvestigacion> findActivasByGrupo(Integer idGrupo);
    boolean existsByNombre(String nombreLinea);
}
