package com.sgi.fiis.observations.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ObservationJpaEntity Unit Tests")
class ObservationJpaEntityTest {

    @Test
    @DisplayName("Should create entity using no-args constructor with null fields")
    void testNoArgsConstructor() {
        ObservationJpaEntity entity = new ObservationJpaEntity();
        assertNull(entity.getId());
        assertNull(entity.getProcedureId());
        assertNull(entity.getReviewerId());
        assertNull(entity.getType());
        assertNull(entity.getDescription());
        assertNull(entity.getStatus());
        assertNull(entity.getReviewerRole());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields via setters and getters")
    void testSettersAndGetters() {
        ObservationJpaEntity entity = new ObservationJpaEntity();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(1);
        entity.setProcedureId(10);
        entity.setReviewerId(20);
        entity.setType("TECNICA");
        entity.setDescription("Descripción de prueba");
        entity.setStatus("PENDIENTE");
        entity.setReviewerRole("COORDINADOR_GRUPO");
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertEquals(1, entity.getId());
        assertEquals(10, entity.getProcedureId());
        assertEquals(20, entity.getReviewerId());
        assertEquals("TECNICA", entity.getType());
        assertEquals("Descripción de prueba", entity.getDescription());
        assertEquals("PENDIENTE", entity.getStatus());
        assertEquals("COORDINADOR_GRUPO", entity.getReviewerRole());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create entity using Builder pattern")
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        ObservationJpaEntity entity = ObservationJpaEntity.builder()
                .id(2)
                .procedureId(15)
                .reviewerId(25)
                .type("DOCUMENTAL")
                .description("Falta firma en documento")
                .status("VIGENTE")
                .reviewerRole("DIRECTOR_INVESTIGACION")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(2, entity.getId());
        assertEquals(15, entity.getProcedureId());
        assertEquals(25, entity.getReviewerId());
        assertEquals("DOCUMENTAL", entity.getType());
        assertEquals("Falta firma en documento", entity.getDescription());
        assertEquals("VIGENTE", entity.getStatus());
        assertEquals("DIRECTOR_INVESTIGACION", entity.getReviewerRole());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should verify constructor with all arguments")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ObservationJpaEntity entity = new ObservationJpaEntity(
                5, 30, 40, "FORMATO", "Error de formato", "PENDIENTE", "DECANO", now, now
        );

        assertEquals(5, entity.getId());
        assertEquals(30, entity.getProcedureId());
        assertEquals(40, entity.getReviewerId());
        assertEquals("FORMATO", entity.getType());
        assertEquals("Error de formato", entity.getDescription());
        assertEquals("PENDIENTE", entity.getStatus());
        assertEquals("DECANO", entity.getReviewerRole());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }
}
