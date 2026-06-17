package com.sgi.fiis.proyectos.presentation.mapper;

import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.domain.model.Project;
import com.sgi.fiis.proyectos.domain.model.ProjectStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {

    private final ProjectMapper mapper = new ProjectMapper();

    @Test
    void testContext() {
        assertNotNull(mapper);
    }
}
