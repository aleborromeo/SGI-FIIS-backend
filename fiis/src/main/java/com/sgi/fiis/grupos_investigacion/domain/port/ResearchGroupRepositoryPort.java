package com.sgi.fiis.grupos_investigacion.domain.port;

import com.sgi.fiis.grupos_investigacion.domain.model.ResearchGroup;

import java.util.List;
import java.util.Optional;

public interface ResearchGroupRepositoryPort {
    ResearchGroup save(ResearchGroup group);
    Optional<ResearchGroup> findById(Integer id);
    List<ResearchGroup> findAll();
    boolean existsByCode(String groupCode);
    boolean existsActiveUser(Integer userId);
    boolean existsActiveUserWithRole(Integer userId, String roleCode);
    List<ResearchGroup> findGroupsByLineId(Integer lineId);
}
