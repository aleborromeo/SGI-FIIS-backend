package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import jakarta.persistence.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResearchGroupEntity Unit Tests")
class ResearchGroupEntityTest {

    @Test
    @DisplayName("Should get and set all fields correctly")
    void testGettersAndSetters() {
        ResearchGroupEntity entity = new ResearchGroupEntity();

        entity.setId(1);
        entity.setGroupCode("GI-001");
        entity.setGroupName("Grupo de Inteligencia Artificial");
        entity.setCurrentCoordinatorId(10);
        entity.setActive(true);

        assertEquals(1, entity.getId());
        assertEquals("GI-001", entity.getGroupCode());
        assertEquals("Grupo de Inteligencia Artificial", entity.getGroupName());
        assertEquals(10, entity.getCurrentCoordinatorId());
        assertTrue(entity.isActive());
    }

    @Test
    @DisplayName("Should allow a null coordinator id")
    void testNullCoordinator() {
        ResearchGroupEntity entity = new ResearchGroupEntity();

        entity.setCurrentCoordinatorId(null);

        assertNull(entity.getCurrentCoordinatorId());
    }

    @Test
    @DisplayName("Should default to a new instance with null fields")
    void testDefaultConstructor() {
        ResearchGroupEntity entity = new ResearchGroupEntity();

        assertNull(entity.getId());
        assertNull(entity.getGroupCode());
        assertNull(entity.getGroupName());
        assertNull(entity.getCurrentCoordinatorId());
        assertFalse(entity.isActive());
    }

    @Test
    @DisplayName("Should be mapped to the grupos_investigacion table")
    void testTableMapping() {
        Table table = ResearchGroupEntity.class.getAnnotation(Table.class);

        assertNotNull(table);
        assertEquals("grupos_investigacion", table.name());
    }
}