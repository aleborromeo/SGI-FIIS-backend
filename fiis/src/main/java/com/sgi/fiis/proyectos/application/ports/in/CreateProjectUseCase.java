package com.sgi.fiis.proyectos.application.ports.in;

import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import java.util.List;

public interface CreateProjectUseCase {
    ProjectResponse execute(CreateProjectRequest request);
    List<ProjectResponse> getProjectsByResponsible(Long responsibleId);
    List<ProjectResponse> getProjectsByGroup(Integer groupId);
    List<ProjectResponse> getAllProjects();
    ProjectResponse getProjectById(Integer id);
}
