package com.sgi.fiis.proyectos.presentation.mapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProjectMapperTest {

    private final ProjectMapper mapper = new ProjectMapper();

    @Test
    void testContext() {
        assertNotNull(mapper);
    }
}
