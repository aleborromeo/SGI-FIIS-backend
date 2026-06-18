package com.sgi.fiis.observations.infrastructure.persistence.mapper;

import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.model.ObservationStatus;
import com.sgi.fiis.observations.domain.model.ObservationType;
import com.sgi.fiis.observations.infrastructure.persistence.entity.ObservationJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ObservationMapper Unit Tests")
class ObservationMapperTest {

    private final ObservationMapper mapper = new ObservationMapper();

    @Test
    @DisplayName("Should map JPA Entity to Domain Model")
    void shouldMapEntityToDomain() {
        LocalDateTime ahora = LocalDateTime.now();
        ObservationJpaEntity entity = ObservationJpaEntity.builder()
                .id(1)
                .procedureId(10)
                .reviewerId(20)
                .type("TECNICA")
                .description("Falta firma")
                .status("PENDIENTE")
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        Observation domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1, domain.getId());
        assertEquals(10, domain.getProcedureId());
        assertEquals(20, domain.getReviewerId());
        assertEquals(ObservationType.TECHNICAL, domain.getType());
        assertEquals("Falta firma", domain.getDescription());
        assertEquals(ObservationStatus.PENDING, domain.getStatus());
        assertEquals("COORDINADOR_GRUPO", domain.getReviewerRole());
        assertEquals(ahora, domain.getCreatedAt());
        assertEquals(ahora, domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map Domain Model to JPA Entity")
    void shouldMapDomainToEntity() {
        LocalDateTime ahora = LocalDateTime.now();
        Observation domain = Observation.builder()
                .id(1)
                .procedureId(10)
                .reviewerId(20)
                .type(ObservationType.TECHNICAL)
                .description("Falta firma")
                .status(ObservationStatus.PENDING)
                .reviewerRole("COORDINADOR_GRUPO")
                .createdAt(ahora)
                .updatedAt(ahora)
                .build();

        ObservationJpaEntity entity = mapper.toJpa(domain);

        assertNotNull(entity);
        assertEquals(1, entity.getId());
        assertEquals(10, entity.getProcedureId());
        assertEquals(20, entity.getReviewerId());
        assertEquals("TECNICA", entity.getType());
        assertEquals("Falta firma", entity.getDescription());
        assertEquals("PENDIENTE", entity.getStatus());
        assertEquals("COORDINADOR_GRUPO", entity.getReviewerRole());
        assertEquals(ahora, entity.getCreatedAt());
        assertEquals(ahora, entity.getUpdatedAt());
    }
}
