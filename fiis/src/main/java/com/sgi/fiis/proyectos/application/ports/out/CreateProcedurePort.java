package com.sgi.fiis.proyectos.application.ports.out;

import com.sgi.fiis.proyectos.domain.model.Project;

public interface CreateProcedurePort {
    void createPostulationProcedure(Project project);
}
