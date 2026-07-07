package com.sgi.fiis.grupos_investigacion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResearchGroup Domain Unit Tests")
class ResearchGroupTest {

    @Test
    @DisplayName("Should build a ResearchGroup with all fields set")
    void shouldBuildResearchGroup() {
        ResearchGroup group = ResearchGroup.builder()
                .id(1)
                .groupCode("GI-001")
                .groupName("Grupo de Inteligencia Artificial")
                .currentCoordinatorId(10)
                .coordinatorFirstNames("Maria")
                .coordinatorLastNames("Lopez")
                .active(true)
                .build();

        assertEquals(1, group.getId());
        assertEquals("GI-001", group.getGroupCode());
        assertEquals("Grupo de Inteligencia Artificial", group.getGroupName());
        assertEquals(10, group.getCurrentCoordinatorId());
        assertEquals("Maria", group.getCoordinatorFirstNames());
        assertEquals("Lopez", group.getCoordinatorLastNames());
        assertTrue(group.isActive());
    }

    @Test
    @DisplayName("Should get and set all fields correctly")
    void testGettersAndSetters() {
        ResearchGroup group = new ResearchGroup();

        group.setId(1);
        group.setGroupCode("GI-001");
        group.setGroupName("Grupo de Inteligencia Artificial");
        group.setCurrentCoordinatorId(10);
        group.setCoordinatorFirstNames("Maria");
        group.setCoordinatorLastNames("Lopez");
        group.setActive(true);

        assertEquals(1, group.getId());
        assertEquals("GI-001", group.getGroupCode());
        assertEquals("Grupo de Inteligencia Artificial", group.getGroupName());
        assertEquals(10, group.getCurrentCoordinatorId());
        assertEquals("Maria", group.getCoordinatorFirstNames());
        assertEquals("Lopez", group.getCoordinatorLastNames());
        assertTrue(group.isActive());
    }

    @Test
    @DisplayName("Should allow a group without a coordinator assigned")
    void shouldAllowGroupWithoutCoordinator() {
        ResearchGroup group = ResearchGroup.builder()
                .id(1)
                .groupCode("GI-001")
                .groupName("Grupo de Inteligencia Artificial")
                .active(true)
                .build();

        assertNull(group.getCurrentCoordinatorId());
        assertNull(group.getCoordinatorFirstNames());
        assertNull(group.getCoordinatorLastNames());
    }

    @Test
    @DisplayName("Should default to a new instance with null and false fields")
    void testDefaultConstructor() {
        ResearchGroup group = new ResearchGroup();

        assertNull(group.getId());
        assertNull(group.getGroupCode());
        assertNull(group.getGroupName());
        assertNull(group.getCurrentCoordinatorId());
        assertNull(group.getCoordinatorFirstNames());
        assertNull(group.getCoordinatorLastNames());
        assertFalse(group.isActive());
    }

    @Test
    @DisplayName("Should consider two groups with the same field values as equal")
    void shouldBeEqualWhenFieldsMatch() {
        ResearchGroup group1 = ResearchGroup.builder()
                .id(1)
                .groupCode("GI-001")
                .groupName("Grupo de Inteligencia Artificial")
                .active(true)
                .build();

        ResearchGroup group2 = ResearchGroup.builder()
                .id(1)
                .groupCode("GI-001")
                .groupName("Grupo de Inteligencia Artificial")
                .active(true)
                .build();

        assertEquals(group1, group2);
        assertEquals(group1.hashCode(), group2.hashCode());
    }
}