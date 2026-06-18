package com.sgi.fiis.observations.infrastructure.persistence.mapper;

import com.sgi.fiis.observations.domain.model.Remedy;
import com.sgi.fiis.observations.infrastructure.persistence.entity.RemedyJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RemedyMapper Unit Tests")
class RemedyMapperTest {

    private final RemedyMapper mapper = new RemedyMapper();

    @Test
    @DisplayName("Should map JPA Entity to Domain Model")
    void shouldMapEntityToDomain() {
        LocalDateTime ahora = LocalDateTime.now();
        RemedyJpaEntity entity = RemedyJpaEntity.builder()
                .id(1)
                .observationId(100)
                .applicantId(5)
                .description("He subido el documento")
                .attachedDocumentId(45)
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        Remedy domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1, domain.getId());
        assertEquals(100, domain.getObservationId());
        assertEquals(5, domain.getApplicantId());
        assertEquals("He subido el documento", domain.getDescription());
        assertEquals(45, domain.getAttachedDocumentId());
        assertEquals(ahora, domain.getCreatedAt());
        assertEquals(ahora, domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map Domain Model to JPA Entity")
    void shouldMapDomainToEntity() {
        LocalDateTime ahora = LocalDateTime.now();
        Remedy domain = Remedy.builder()
                .id(1)
                .observationId(100)
                .applicantId(5)
                .description("He subido el documento")
                .attachedDocumentId(45)
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        RemedyJpaEntity entity = mapper.toJpa(domain);

        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals(100, entity.getObservationId());
        assertEquals(5, entity.getApplicantId());
        assertEquals("He subido el documento", entity.getDescription());
        assertEquals(45, entity.getAttachedDocumentId());
        assertEquals(ahora, entity.getCreatedAt());
        assertEquals(ahora, entity.getUpdatedAt());
    }
}
