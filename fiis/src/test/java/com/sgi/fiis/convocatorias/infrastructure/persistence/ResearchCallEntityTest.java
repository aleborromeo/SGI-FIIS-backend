package com.sgi.fiis.convocatorias.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResearchCallEntity Unit Tests")
class ResearchCallEntityTest {

    @Test
    @DisplayName("Should create entity using no-args constructor")
    void testNoArgsConstructor() {
        ResearchCallEntity entity = new ResearchCallEntity();
        assertNull(entity.getId());
        assertNull(entity.getStatus());
    }

    @Test
    @DisplayName("Should create entity using all-args constructor")
    void testAllArgsConstructor() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 31);

        ResearchCallEntity entity = new ResearchCallEntity(
                1, "Convocatoria 2025-I", "Description", startDate, endDate,
                "ABIERTA", null, null, null, null
        );

        assertEquals(1, entity.getId());
        assertEquals("Convocatoria 2025-I", entity.getTitle());
        assertEquals(startDate, entity.getStartDate());
        assertEquals(endDate, entity.getEndDate());
        assertEquals("ABIERTA", entity.getStatus());
        assertNull(entity.getCreatedAt());
    }

    @Test
    @DisplayName("Should create entity using builder")
    void testBuilder() {
        LocalDate startDate = LocalDate.of(2025, 4, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);

        ResearchCallEntity entity = ResearchCallEntity.builder()
                .id(2)
                .title("Convocatoria 2025-II")
                .description("Description")
                .startDate(startDate)
                .endDate(endDate)
                .status("CERRADA")
                .build();

        assertEquals(2, entity.getId());
        assertEquals("Convocatoria 2025-II", entity.getTitle());
        assertEquals(startDate, entity.getStartDate());
        assertEquals(endDate, entity.getEndDate());
        assertEquals("CERRADA", entity.getStatus());
        assertNull(entity.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields via setters")
    void testSettersAndGetters() {
        ResearchCallEntity entity = new ResearchCallEntity();
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 9, 30);

        entity.setId(3);
        entity.setTitle("Convocatoria 2025-III");
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        entity.setStatus("EN_EVALUACION");

        assertEquals(3, entity.getId());
        assertEquals("Convocatoria 2025-III", entity.getTitle());
        assertEquals(startDate, entity.getStartDate());
        assertEquals(endDate, entity.getEndDate());
        assertEquals("EN_EVALUACION", entity.getStatus());
    }

    @Test
    @DisplayName("Should set createdAt and default status to ABIERTA on PrePersist when status is null")
    void testOnCreateWithNullStatus() {
        ResearchCallEntity entity = new ResearchCallEntity();
        assertNull(entity.getStatus());
        assertNull(entity.getCreatedAt());

        entity.onCreate();

        assertNotNull(entity.getCreatedAt());
        assertEquals("ABIERTA", entity.getStatus());
    }

    @Test
    @DisplayName("Should not overwrite existing status on PrePersist")
    void testOnCreateWithExistingStatus() {
        ResearchCallEntity entity = new ResearchCallEntity();
        entity.setStatus("CERRADA");

        entity.onCreate();

        assertNotNull(entity.getCreatedAt());
        assertEquals("CERRADA", entity.getStatus());
    }

    @Test
    @DisplayName("Should handle toString, equals, and hashCode from @Data")
    void testDataAnnotationMethods() {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 31);

        ResearchCallEntity entity1 = new ResearchCallEntity(
                1, "Test", "Description", startDate, endDate, "ABIERTA", null, null, null, null
        );
        ResearchCallEntity entity2 = new ResearchCallEntity(
                1, "Test", "Description", startDate, endDate, "ABIERTA", null, null, null, null
        );

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
        assertNotNull(entity1.toString());
        assertTrue(entity1.toString().contains("Test"));
    }
}
