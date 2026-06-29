package com.sgi.fiis.proyectos.infrastructure.persistence;

import com.sgi.fiis.convocatorias.infrastructure.persistence.ResearchCallEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.lineas_investigacion.infrastructure.persistence.ResearchLineEntity;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProjectEntity Unit Tests")
class ProjectEntityTest {

    @Test
    @DisplayName("Should create entity using no-args constructor")
    void testNoArgsConstructor() {
        ProjectEntity entity = new ProjectEntity();
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Should create entity using all-args constructor")
    void testAllArgsConstructor() {
        ResearchLineEntity line = new ResearchLineEntity();
        ResearchGroupEntity group = new ResearchGroupEntity();
        UserEntity responsible = new UserEntity();
        ResearchCallEntity call = new ResearchCallEntity();
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);

        ProjectEntity entity = new ProjectEntity(
                1, "PROJ-001", "Test Project", "Summary",
                "General Objective", null, null, null, null,
                line, group, BigDecimal.valueOf(10000),
                startDate, endDate, "Lima", responsible, call,
                42, "ACTIVO", null, null
        );

        assertEquals(1, entity.getId());
        assertEquals("PROJ-001", entity.getCode());
        assertEquals("Test Project", entity.getTitle());
        assertEquals("Summary", entity.getSummary());
        assertEquals("General Objective", entity.getGeneralObjective());
        assertSame(line, entity.getResearchLine());
        assertSame(group, entity.getGroup());
        assertEquals(BigDecimal.valueOf(10000), entity.getBudget());
        assertEquals(startDate, entity.getStartDate());
        assertEquals(endDate, entity.getEndDate());
        assertEquals("Lima", entity.getExecutionPlace());
        assertSame(responsible, entity.getResponsible());
        assertSame(call, entity.getResearchCall());
        assertEquals(42, entity.getDocumentId());
        assertEquals("ACTIVO", entity.getStatus());
    }

    @Test
    @DisplayName("Should create entity using builder")
    void testBuilder() {
        ResearchLineEntity line = new ResearchLineEntity();
        ResearchGroupEntity group = new ResearchGroupEntity();
        UserEntity responsible = new UserEntity();
        LocalDate startDate = LocalDate.of(2025, 3, 1);
        LocalDate endDate = LocalDate.of(2025, 8, 30);

        ProjectEntity entity = ProjectEntity.builder()
                .id(2)
                .code("PROJ-002")
                .title("Builder Project")
                .summary("Builder Summary")
                .generalObjective("Builder Objective")
                .researchLine(line)
                .group(group)
                .budget(BigDecimal.valueOf(25000.50))
                .startDate(startDate)
                .endDate(endDate)
                .executionPlace("Callao")
                .responsible(responsible)
                .documentId(99)
                .status("EN_EJECUCION")
                .build();

        assertEquals(2, entity.getId());
        assertEquals("PROJ-002", entity.getCode());
        assertEquals("Builder Project", entity.getTitle());
        assertEquals("Builder Summary", entity.getSummary());
        assertEquals("Builder Objective", entity.getGeneralObjective());
        assertSame(line, entity.getResearchLine());
        assertSame(group, entity.getGroup());
        assertEquals(BigDecimal.valueOf(25000.50), entity.getBudget());
        assertEquals(startDate, entity.getStartDate());
        assertEquals(endDate, entity.getEndDate());
        assertEquals("Callao", entity.getExecutionPlace());
        assertSame(responsible, entity.getResponsible());
        assertNull(entity.getResearchCall());
        assertEquals(99, entity.getDocumentId());
        assertEquals("EN_EJECUCION", entity.getStatus());
    }

    @Test
    @DisplayName("Should set and get all fields via setters")
    void testSettersAndGetters() {
        ProjectEntity entity = new ProjectEntity();
        ResearchLineEntity line = new ResearchLineEntity();
        ResearchGroupEntity group = new ResearchGroupEntity();
        UserEntity responsible = new UserEntity();

        entity.setId(3);
        entity.setCode("PROJ-003");
        entity.setTitle("Setter Project");
        entity.setSummary("Setter Summary");
        entity.setGeneralObjective("Setter Objective");
        entity.setResearchLine(line);
        entity.setGroup(group);
        entity.setBudget(BigDecimal.valueOf(5000));
        entity.setStartDate(LocalDate.of(2025, 5, 1));
        entity.setEndDate(LocalDate.of(2025, 6, 1));
        entity.setExecutionPlace("Huancayo");
        entity.setResponsible(responsible);
        entity.setDocumentId(55);
        entity.setStatus("FINALIZADO");

        assertEquals(3, entity.getId());
        assertEquals("PROJ-003", entity.getCode());
        assertEquals("Setter Project", entity.getTitle());
        assertEquals("Setter Summary", entity.getSummary());
        assertEquals("Setter Objective", entity.getGeneralObjective());
        assertSame(line, entity.getResearchLine());
        assertSame(group, entity.getGroup());
        assertEquals(BigDecimal.valueOf(5000), entity.getBudget());
        assertEquals(LocalDate.of(2025, 5, 1), entity.getStartDate());
        assertEquals(LocalDate.of(2025, 6, 1), entity.getEndDate());
        assertEquals("Huancayo", entity.getExecutionPlace());
        assertSame(responsible, entity.getResponsible());
        assertEquals(55, entity.getDocumentId());
        assertEquals("FINALIZADO", entity.getStatus());
    }

    @Test
    @DisplayName("Should set createdAt and updatedAt on PrePersist")
    void testOnCreate() {
        ProjectEntity entity = new ProjectEntity();
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());

        entity.onCreate();

        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertTrue(entity.getCreatedAt() instanceof LocalDateTime);
        assertTrue(entity.getUpdatedAt() instanceof LocalDateTime);
    }

    @Test
    @DisplayName("Should update only updatedAt on PreUpdate")
    void testOnUpdate() {
        ProjectEntity entity = new ProjectEntity();
        entity.setCreatedAt(LocalDateTime.of(2025, 1, 1, 10, 0));
        entity.onUpdate();

        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
    }
}
