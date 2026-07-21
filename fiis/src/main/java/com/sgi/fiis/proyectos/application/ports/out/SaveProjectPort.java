package com.sgi.fiis.proyectos.application.ports.out;

import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectMember;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;

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

    void saveMembers(Integer projectId, List<ProjectMember> members);

    List<ProjectMember> findMembersByProjectId(Integer projectId);

    List<Project> findByResponsibleIdAndStatus(Long responsibleId, ProjectStatus status);

    void deleteById(Integer projectId);

}
