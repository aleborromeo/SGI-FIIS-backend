package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import jakarta.persistence.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResearchGroupLineEntity Unit Tests")
class ResearchGroupLineEntityTest {

    @Test
    @DisplayName("Should create an entity with the all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        ResearchGroupLineEntity entity = new ResearchGroupLineEntity(1, 2);

        assertEquals(1, entity.getGroupId());
        assertEquals(2, entity.getLineId());
    }

    @Test
    @DisplayName("Should get and set fields correctly")
    void testGettersAndSetters() {
        ResearchGroupLineEntity entity = new ResearchGroupLineEntity();

        entity.setGroupId(1);
        entity.setLineId(2);

        assertEquals(1, entity.getGroupId());
        assertEquals(2, entity.getLineId());
    }

    @Test
    @DisplayName("Should default to a new instance with null fields")
    void testDefaultConstructor() {
        ResearchGroupLineEntity entity = new ResearchGroupLineEntity();

        assertNull(entity.getGroupId());
        assertNull(entity.getLineId());
    }

    @Test
    @DisplayName("Should be mapped to the lineas_por_grupo table")
    void testTableMapping() {
        Table table = ResearchGroupLineEntity.class.getAnnotation(Table.class);

        assertNotNull(table);
        assertEquals("lineas_por_grupo", table.name());
    }
}