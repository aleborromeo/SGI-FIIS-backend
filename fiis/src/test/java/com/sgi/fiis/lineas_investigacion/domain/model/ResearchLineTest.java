package com.sgi.fiis.lineas_investigacion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResearchLine Domain Unit Tests")
class ResearchLineTest {

    @Test
    @DisplayName("Should build a ResearchLine with all fields set")
    void shouldBuildResearchLine() {
        LocalDateTime createdAt = LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0);

        ResearchLine line = ResearchLine.builder()
                .id(1)
                .lineName("Inteligencia Artificial")
                .active(true)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();

        assertEquals(1, line.getId());
        assertEquals("Inteligencia Artificial", line.getLineName());
        assertTrue(line.isActive());
        assertEquals(createdAt, line.getCreatedAt());
        assertEquals(createdAt, line.getUpdatedAt());
    }

    @Test
    @DisplayName("Should activate an inactive line and update updatedAt")
    void shouldActivateLine() {
        ResearchLine line = ResearchLine.builder()
                .id(1)
                .lineName("Inteligencia Artificial")
                .active(false)
                .build();

        LocalDateTime before = LocalDateTime.now();
        line.activate();
        LocalDateTime after = LocalDateTime.now();

        assertTrue(line.isActive());
        assertNotNull(line.getUpdatedAt());
        assertFalse(line.getUpdatedAt().isBefore(before));
        assertFalse(line.getUpdatedAt().isAfter(after));
    }

    @Test
    @DisplayName("Should deactivate an active line and update updatedAt")
    void shouldDeactivateLine() {
        ResearchLine line = ResearchLine.builder()
                .id(1)
                .lineName("Inteligencia Artificial")
                .active(true)
                .build();

        LocalDateTime before = LocalDateTime.now();
        line.deactivate();
        LocalDateTime after = LocalDateTime.now();

        assertFalse(line.isActive());
        assertNotNull(line.getUpdatedAt());
        assertFalse(line.getUpdatedAt().isBefore(before));
        assertFalse(line.getUpdatedAt().isAfter(after));
    }

    @Test
    @DisplayName("Should default to a new instance with null and false fields")
    void testDefaultConstructor() {
        ResearchLine line = new ResearchLine();

        assertNull(line.getId());
        assertNull(line.getLineName());
        assertFalse(line.isActive());
        assertNull(line.getCreatedAt());
        assertNull(line.getUpdatedAt());
    }
}