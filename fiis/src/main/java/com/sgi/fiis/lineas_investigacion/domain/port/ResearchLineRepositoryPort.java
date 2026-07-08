package com.sgi.fiis.lineas_investigacion.domain.port;

import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;

import java.util.List;
import java.util.Optional;

public interface ResearchLineRepositoryPort {
    ResearchLine save(ResearchLine line);
    Optional<ResearchLine> findById(Integer id);
    List<ResearchLine> findAll();
    List<ResearchLine> findAllActive();
    List<ResearchLine> findActiveByGroup(Integer groupId);
    boolean existsByName(String lineName);
}
