package com.sgi.fiis.tramites.infrastructure.persistence;

import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcedureEntity Unit Tests")
class ProcedureEntityTest {

    @Test
    @DisplayName("Should create entity using no-args constructor")
    void testNoArgsConstructor() {
        ProcedureEntity entity = new ProcedureEntity();
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Should create entity using all-args constructor")
    void testAllArgsConstructor() {
        UserEntity applicant = new UserEntity();
        ResearchGroupEntity group = new ResearchGroupEntity();
        ProjectEntity projectRef = new ProjectEntity();

        ProcedureEntity entity = new ProcedureEntity(
                1, "TRAM-001", "SOLICITUD_CREACION",
                applicant, group, "PENDIENTE",
                "COORDINADOR", null, null, projectRef
        );

        assertEquals(1, entity.getId());
        assertEquals("TRAM-001", entity.getCode());
        assertEquals("SOLICITUD_CREACION", entity.getProcedureType());
        assertSame(applicant, entity.getApplicant());
        assertSame(group, entity.getGroup());
        assertEquals("PENDIENTE", entity.getStatus());
        assertEquals("COORDINADOR", entity.getReviewerRole());
        assertSame(projectRef, entity.getProjectReference());
        assertNull(entity.getSentAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create entity using builder")
    void testBuilder() {
        UserEntity applicant = new UserEntity();
        ResearchGroupEntity group = new ResearchGroupEntity();

        ProcedureEntity entity = ProcedureEntity.builder()
                .id(2)
                .code("TRAM-002")
                .procedureType("SOLICITUD_MODIFICACION")
                .applicant(applicant)
                .group(group)
                .status("EN_REVISION")
                .reviewerRole("DIRECTOR")
                .build();

        assertEquals(2, entity.getId());
        assertEquals("TRAM-002", entity.getCode());
        assertEquals("SOLICITUD_MODIFICACION", entity.getProcedureType());
        assertSame(applicant, entity.getApplicant());
        assertSame(group, entity.getGroup());
        assertEquals("EN_REVISION", entity.getStatus());
        assertEquals("DIRECTOR", entity.getReviewerRole());
        assertNull(entity.getProjectReference());
    }

    @Test
    @DisplayName("Should set and get all fields via setters")
    void testSettersAndGetters() {
        ProcedureEntity entity = new ProcedureEntity();
        UserEntity applicant = new UserEntity();
        ResearchGroupEntity group = new ResearchGroupEntity();
        ProjectEntity projectRef = new ProjectEntity();

        entity.setId(3);
        entity.setCode("TRAM-003");
        entity.setProcedureType("SOLICITUD_CIERRE");
        entity.setApplicant(applicant);
        entity.setGroup(group);
        entity.setStatus("APROBADO");
        entity.setReviewerRole("DECANO");
        entity.setProjectReference(projectRef);

        assertEquals(3, entity.getId());
        assertEquals("TRAM-003", entity.getCode());
        assertEquals("SOLICITUD_CIERRE", entity.getProcedureType());
        assertSame(applicant, entity.getApplicant());
        assertSame(group, entity.getGroup());
        assertEquals("APROBADO", entity.getStatus());
        assertEquals("DECANO", entity.getReviewerRole());
        assertSame(projectRef, entity.getProjectReference());
    }

    @Test
    @DisplayName("Should set sentAt and updatedAt on PrePersist")
    void testOnCreate() {
        ProcedureEntity entity = new ProcedureEntity();
        assertNull(entity.getSentAt());
        assertNull(entity.getUpdatedAt());

        entity.onCreate();

        assertNotNull(entity.getSentAt());
        assertNotNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update only updatedAt on PreUpdate")
    void testOnUpdate() {
        ProcedureEntity entity = new ProcedureEntity();
        entity.setSentAt(LocalDateTime.of(2025, 1, 1, 10, 0));
        entity.onUpdate();

        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), entity.getSentAt());
        assertNotNull(entity.getUpdatedAt());
    }
}
