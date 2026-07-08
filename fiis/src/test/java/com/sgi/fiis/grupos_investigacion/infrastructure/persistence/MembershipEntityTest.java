package com.sgi.fiis.grupos_investigacion.infrastructure.persistence;

import jakarta.persistence.Table;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MembershipEntity Unit Tests")
class MembershipEntityTest {

    @Test
    @DisplayName("Should get and set all fields correctly")
    void testGettersAndSetters() {
        MembershipEntity entity = new MembershipEntity();
        LocalDateTime start = LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, Month.DECEMBER, 31, 23, 59);

        entity.setId(1);
        entity.setGroupId(2);
        entity.setUserId(3);
        entity.setActive(true);
        entity.setStartDate(start);
        entity.setEndDate(end);

        assertEquals(1, entity.getId());
        assertEquals(2, entity.getGroupId());
        assertEquals(3, entity.getUserId());
        assertTrue(entity.isActive());
        assertEquals(start, entity.getStartDate());
        assertEquals(end, entity.getEndDate());
    }

    @Test
    @DisplayName("Should allow a null end date for an ongoing membership")
    void testNullEndDate() {
        MembershipEntity entity = new MembershipEntity();

        entity.setEndDate(null);

        assertNull(entity.getEndDate());
    }

    @Test
    @DisplayName("Should default to a new instance with null fields")
    void testDefaultConstructor() {
        MembershipEntity entity = new MembershipEntity();

        assertNull(entity.getId());
        assertNull(entity.getGroupId());
        assertNull(entity.getUserId());
        assertFalse(entity.isActive());
        assertNull(entity.getStartDate());
        assertNull(entity.getEndDate());
    }

    @Test
    @DisplayName("Should be mapped to the membresias_grupo table")
    void testTableMapping() {
        Table table = MembershipEntity.class.getAnnotation(Table.class);

        assertNotNull(table);
        assertEquals("membresias_grupo", table.name());
    }
}