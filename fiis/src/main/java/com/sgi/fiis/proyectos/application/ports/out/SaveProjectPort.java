package com.sgi.fiis.proyectos.application.ports.out;

import com.sgi.fiis.proyectos.domain.model.Project;
import java.util.List;
import java.util.Optional;

public interface SaveProjectPort {
    Project save(Project project);
    List<Project> findByResponsibleId(Long responsibleId);
    List<Project> findAll();
    Optional<Project> findById(Integer id);
    Optional<String> getGroupCode(Integer groupId);
    Optional<String> getLineName(Integer lineId);
    List<Project> findByGroupId(Integer groupId);
    boolean isUserMemberOfGroup(Long userId, Integer groupId);
    boolean isGroupActive(Integer groupId);
    boolean isLineActive(Integer lineId);
}
