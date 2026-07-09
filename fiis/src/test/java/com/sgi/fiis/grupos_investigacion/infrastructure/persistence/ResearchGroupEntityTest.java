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
        com.sgi.fiis.users.infrastructure.persistence.UserEntity user = new com.sgi.fiis.users.infrastructure.persistence.UserEntity();
        user.setId(10L);

        entity.setId(1);
        entity.setCode("GI-001");
        entity.setName("Grupo de Inteligencia Artificial");
        entity.setCurrentCoordinator(user);
        entity.setActive(true);

        assertEquals(1, entity.getId());
        assertEquals("GI-001", entity.getCode());
        assertEquals("Grupo de Inteligencia Artificial", entity.getName());
        assertEquals(10L, entity.getCurrentCoordinator().getId());
        assertTrue(entity.isActive());
    }

    @Test
    @DisplayName("Should allow a null coordinator")
    void testNullCoordinator() {
        ResearchGroupEntity entity = new ResearchGroupEntity();

        entity.setCurrentCoordinator(null);

        assertNull(entity.getCurrentCoordinator());
    }

    @Test
    @DisplayName("Should default to a new instance with null fields")
    void testDefaultConstructor() {
        ResearchGroupEntity entity = new ResearchGroupEntity();

        assertNull(entity.getId());
        assertNull(entity.getCode());
        assertNull(entity.getName());
        assertNull(entity.getCurrentCoordinator());
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