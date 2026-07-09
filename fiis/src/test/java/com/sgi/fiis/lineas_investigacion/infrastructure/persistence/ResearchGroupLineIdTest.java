package com.sgi.fiis.lineas_investigacion.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResearchGroupLineId Unit Tests")
class ResearchGroupLineIdTest {

    @Test
    @DisplayName("Should create an id with the all-args constructor")
    void shouldCreateWithAllArgsConstructor() {
        ResearchGroupLineId id = new ResearchGroupLineId(1, 2);

        assertEquals(1, id.getGroupId());
        assertEquals(2, id.getLineId());
    }

    @Test
    @DisplayName("Should get and set fields correctly")
    void testGettersAndSetters() {
        ResearchGroupLineId id = new ResearchGroupLineId();

        id.setGroupId(1);
        id.setLineId(2);

        assertEquals(1, id.getGroupId());
        assertEquals(2, id.getLineId());
    }

    @Test
    @DisplayName("Should be equal when groupId and lineId match")
    void shouldBeEqualWhenFieldsMatch() {
        ResearchGroupLineId id1 = new ResearchGroupLineId(1, 2);
        ResearchGroupLineId id2 = new ResearchGroupLineId(1, 2);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when groupId or lineId differ")
    void shouldNotBeEqualWhenFieldsDiffer() {
        ResearchGroupLineId id1 = new ResearchGroupLineId(1, 2);
        ResearchGroupLineId id2 = new ResearchGroupLineId(1, 3);
        ResearchGroupLineId id3 = new ResearchGroupLineId(2, 2);

        assertNotEquals(id1, id2);
        assertNotEquals(id1, id3);
    }

    @Test
    @DisplayName("Should not be equal to null or a different type")
    void shouldNotBeEqualToNullOrDifferentType() {
        ResearchGroupLineId id = new ResearchGroupLineId(1, 2);

        assertNotEquals(null, id);
        assertNotEquals("not-an-id", id);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        ResearchGroupLineId id = new ResearchGroupLineId(1, 2);

        assertTrue(id.equals(id));
    }
}