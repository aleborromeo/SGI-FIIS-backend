package com.sgi.fiis.observations.infrastructure.persistence.mapper;

import com.sgi.fiis.observations.domain.model.Observation;
import com.sgi.fiis.observations.domain.model.ObservationStatus;
import com.sgi.fiis.observations.domain.model.ObservationType;
import com.sgi.fiis.observations.infrastructure.persistence.entity.ObservationJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ObservationMapper Unit Tests")
class ObservationMapperTest {

    private ObservationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObservationMapper();
    }

    @Test
    @DisplayName("Should return null when mapping null entity to domain")
    void toDomain_nullEntity_shouldReturnNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("Should successfully map JPA Entity to Domain Model")
    void toDomain_validEntity_shouldMapCorrectly() {
        ObservationJpaEntity entity = ObservationJpaEntity.builder()
                .id(1)
                .procedureId(2)
                .reviewerId(3)
                .type("TECNICA")
                .description("Technical description")
                .status("PENDIENTE")
                .reviewerRole("ROLE_EVALUADOR")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Observation domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1, domain.getId().intValue());
        assertEquals(2, domain.getProcedureId().intValue());
        assertEquals(3, domain.getReviewerId().intValue());
        assertEquals(ObservationType.TECNICA, domain.getType());
        assertEquals("Technical description", domain.getDescription());
        assertEquals(ObservationStatus.PENDIENTE, domain.getStatus());
        assertEquals("ROLE_EVALUADOR", domain.getReviewerRole());
        assertNotNull(domain.getCreatedAt());
        assertNotNull(domain.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map other types and statuses to domain model")
    void toDomain_otherTypesAndStatuses_shouldMapCorrectly() {
        // Documental & Subsanada
        ObservationJpaEntity docEntity = ObservationJpaEntity.builder()
                .type("DOCUMENTAL")
                .status("SUBSANADA")
                .build();
        Observation docDomain = mapper.toDomain(docEntity);
        assertEquals(ObservationType.DOCUMENTAL, docDomain.getType());
        assertEquals(ObservationStatus.SUBSANADA, docDomain.getStatus());

        // Presupuestal & Vigente
        ObservationJpaEntity presEntity = ObservationJpaEntity.builder()
                .type("PRESUPUESTAL")
                .status("VIGENTE")
                .build();
        Observation presDomain = mapper.toDomain(presEntity);
        assertEquals(ObservationType.PRESUPUESTAL, presDomain.getType());
        assertEquals(ObservationStatus.VIGENTE, presDomain.getStatus());

        // Formato & Vigente
        ObservationJpaEntity formEntity = ObservationJpaEntity.builder()
                .type("FORMATO")
                .status("VIGENTE")
                .build();
        Observation formDomain = mapper.toDomain(formEntity);
        assertEquals(ObservationType.FORMATO, formDomain.getType());
    }

    @Test
    @DisplayName("Should return null for status/type when dbStatus/dbType is null")
    void toDomain_nullStatusOrType_shouldReturnNullFields() {
        ObservationJpaEntity entity = ObservationJpaEntity.builder()
                .type(null)
                .status(null)
                .build();

        Observation domain = mapper.toDomain(entity);

        assertNull(domain.getType());
        assertNull(domain.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when DB status is unknown")
    void toDomain_unknownStatus_shouldThrowException() {
        ObservationJpaEntity entity = ObservationJpaEntity.builder()
                .status("UNKNOWN_STATUS")
                .build();

        assertThrows(IllegalArgumentException.class, () -> mapper.toDomain(entity));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when DB type is unknown")
    void toDomain_unknownType_shouldThrowException() {
        ObservationJpaEntity entity = ObservationJpaEntity.builder()
                .type("UNKNOWN_TYPE")
                .build();

        assertThrows(IllegalArgumentException.class, () -> mapper.toDomain(entity));
    }

    @Test
    @DisplayName("Should return null when mapping null domain model to JPA")
    void toJpa_nullDomain_shouldReturnNull() {
        assertNull(mapper.toJpa(null));
    }

    @Test
    @DisplayName("Should successfully map Domain Model to JPA Entity")
    void toJpa_validDomain_shouldMapCorrectly() {
        Observation domain = Observation.builder()
                .id(1)
                .procedureId(2)
                .reviewerId(3)
                .type(ObservationType.TECNICA)
                .description("Technical description")
                .status(ObservationStatus.PENDIENTE)
                .reviewerRole("ROLE_EVALUADOR")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ObservationJpaEntity entity = mapper.toJpa(domain);

        assertNotNull(entity);
        assertEquals(1, entity.getId().intValue());
        assertEquals(2, entity.getProcedureId().intValue());
        assertEquals(3, entity.getReviewerId().intValue());
        assertEquals("TECNICA", entity.getType());
        assertEquals("Technical description", entity.getDescription());
        assertEquals("PENDIENTE", entity.getStatus());
        assertEquals("ROLE_EVALUADOR", entity.getReviewerRole());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should map other domain types and statuses to JPA Entity")
    void toJpa_otherTypesAndStatuses_shouldMapCorrectly() {
        // Documentary & Remedied
        Observation docDomain = Observation.builder()
                .type(ObservationType.DOCUMENTAL)
                .status(ObservationStatus.SUBSANADA)
                .build();
        ObservationJpaEntity docEntity = mapper.toJpa(docDomain);
        assertEquals("DOCUMENTAL", docEntity.getType());
        assertEquals("SUBSANADA", docEntity.getStatus());

        // Budgetary & Active
        Observation presDomain = Observation.builder()
                .type(ObservationType.PRESUPUESTAL)
                .status(ObservationStatus.VIGENTE)
                .build();
        ObservationJpaEntity presEntity = mapper.toJpa(presDomain);
        assertEquals("PRESUPUESTAL", presEntity.getType());
        assertEquals("VIGENTE", presEntity.getStatus());

        // Format & Active
        Observation formDomain = Observation.builder()
                .type(ObservationType.FORMATO)
                .status(ObservationStatus.VIGENTE)
                .build();
        ObservationJpaEntity formEntity = mapper.toJpa(formDomain);
        assertEquals("FORMATO", formEntity.getType());
    }

    @Test
    @DisplayName("Should return null for status/type in JPA when domainStatus/domainType is null")
    void toJpa_nullStatusOrType_shouldReturnNullFields() {
        Observation domain = Observation.builder()
                .type(null)
                .status(null)
                .build();

        ObservationJpaEntity entity = mapper.toJpa(domain);

        assertNull(entity.getType());
        assertNull(entity.getStatus());
    }
}
