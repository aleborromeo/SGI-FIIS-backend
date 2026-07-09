package com.sgi.fiis.observations.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RemedyJpaEntity Unit Tests")
class RemedyJpaEntityTest {

    @Test
    @DisplayName("Should create entity using no-args constructor with null fields")
    void testNoArgsConstructor() {
        RemedyJpaEntity entity = new RemedyJpaEntity();
        assertNull(entity.getId());
        assertNull(entity.getObservationId());
        assertNull(entity.getApplicantId());
        assertNull(entity.getDescription());
        assertNull(entity.getAttachedDocumentId());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields via setters and getters")
    void testSettersAndGetters() {
        RemedyJpaEntity entity = new RemedyJpaEntity();
        LocalDateTime now = LocalDateTime.now();

        entity.setId(1);
        entity.setObservationId(100);
        entity.setApplicantId(200);
        entity.setDescription("Remedio de prueba");
        entity.setAttachedDocumentId(300);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        assertEquals(1, entity.getId());
        assertEquals(100, entity.getObservationId());
        assertEquals(200, entity.getApplicantId());
        assertEquals("Remedio de prueba", entity.getDescription());
        assertEquals(300, entity.getAttachedDocumentId());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create entity using Builder pattern")
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        RemedyJpaEntity entity = RemedyJpaEntity.builder()
                .id(2)
                .observationId(150)
                .applicantId(250)
                .description("Se adjunta el documento corregido")
                .attachedDocumentId(350)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(2, entity.getId());
        assertEquals(150, entity.getObservationId());
        assertEquals(250, entity.getApplicantId());
        assertEquals("Se adjunta el documento corregido", entity.getDescription());
        assertEquals(350, entity.getAttachedDocumentId());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should verify constructor with all arguments")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        RemedyJpaEntity entity = new RemedyJpaEntity(
                5, 120, 220, "Detalles del remedio", 320, now, now
        );

        assertEquals(5, entity.getId());
        assertEquals(120, entity.getObservationId());
        assertEquals(220, entity.getApplicantId());
        assertEquals("Detalles del remedio", entity.getDescription());
        assertEquals(320, entity.getAttachedDocumentId());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(now, entity.getUpdatedAt());
    }
}
