package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import jakarta.persistence.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GroupMembershipEntity Unit Tests")
class GroupMembershipEntityTest {

    private ResearchGroupEntity getTestGroup() {
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(1);
        group.setCode("GI-001");
        group.setName("Grupo de Inteligencia Artificial");
        return group;
    }

    @Test
    @DisplayName("Should get and set all fields correctly")
    void testGettersAndSetters() {
        GroupMembershipEntity entity = new GroupMembershipEntity();
        ResearchGroupEntity group = getTestGroup();
        LocalDateTime start = LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59);

        entity.setId(1);
        entity.setGroup(group);
        entity.setUserId(3L);
        entity.setActive(true);
        entity.setStartDate(start);
        entity.setEndDate(end);

        assertEquals(1, entity.getId());
        assertEquals(group, entity.getGroup());
        assertEquals(3L, entity.getUserId());
        assertTrue(entity.getActive());
        assertEquals(start, entity.getStartDate());
        assertEquals(end, entity.getEndDate());
    }

    @Test
    @DisplayName("Should allow a null end date for an ongoing membership")
    void testNullEndDate() {
        GroupMembershipEntity entity = new GroupMembershipEntity();

        entity.setEndDate(null);

        assertNull(entity.getEndDate());
    }

    @Test
    @DisplayName("Should default to a new instance with null fields")
    void testDefaultConstructor() {
        GroupMembershipEntity entity = new GroupMembershipEntity();

        assertNull(entity.getId());
        assertNull(entity.getGroup());
        assertNull(entity.getUserId());
        assertNull(entity.getActive());
        assertNull(entity.getStartDate());
        assertNull(entity.getEndDate());
    }

    @Test
    @DisplayName("Should be mapped to the membresias_grupo table")
    void testTableMapping() {
        Table table = GroupMembershipEntity.class.getAnnotation(Table.class);

        assertNotNull(table);
        assertEquals("membresias_grupo", table.name());
    }
}
