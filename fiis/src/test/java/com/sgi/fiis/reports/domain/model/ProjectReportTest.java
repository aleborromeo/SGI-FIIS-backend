package com.sgi.fiis.reports.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ProjectReport}.
 * Verifies that the default constructor and all getters/setters work correctly.
 */
@DisplayName("ProjectReport - Unit Tests")
class ProjectReportTest {

    @Test
    @DisplayName("Default constructor should create empty instance without errors")
    void constructor_createsEmptyInstance() {
        ProjectReport r = new ProjectReport();
        assertThat(r).isNotNull();
    }

    @Test
    @DisplayName("Getters and setters should work correctly for all fields")
    void gettersAndSetters_workCorrectly() {
        ProjectReport r = new ProjectReport();

        LocalDate start = LocalDate.of(2024, Month.MARCH, 1);
        LocalDate end   = LocalDate.of(2024, Month.DECEMBER, 31);
        LocalDateTime created = LocalDateTime.of(2024, Month.FEBRUARY, 15, 10, 30);

        r.setProjectId(1);
        r.setProjectCode("PRY-2024-001");
        r.setProjectTitle("Sistema de Gestión");
        r.setProjectStatus("APROBADO");
        r.setGroupName("Grupo IA");
        r.setLineName("Ingeniería de Software");
        r.setResponsibleName("Juan Pérez");
        r.setCallTitle("Convocatoria 2024-I");
        r.setBudget(BigDecimal.valueOf(15000.50));
        r.setStartDate(start);
        r.setEndDate(end);
        r.setCreatedAt(created);

        assertThat(r.getProjectId()).isEqualTo(1);
        assertThat(r.getProjectCode()).isEqualTo("PRY-2024-001");
        assertThat(r.getProjectTitle()).isEqualTo("Sistema de Gestión");
        assertThat(r.getProjectStatus()).isEqualTo("APROBADO");
        assertThat(r.getGroupName()).isEqualTo("Grupo IA");
        assertThat(r.getLineName()).isEqualTo("Ingeniería de Software");
        assertThat(r.getResponsibleName()).isEqualTo("Juan Pérez");
        assertThat(r.getCallTitle()).isEqualTo("Convocatoria 2024-I");
        assertThat(r.getBudget()).isEqualByComparingTo(BigDecimal.valueOf(15000.50));
        assertThat(r.getStartDate()).isEqualTo(start);
        assertThat(r.getEndDate()).isEqualTo(end);
        assertThat(r.getCreatedAt()).isEqualTo(created);
    }

    @Test
    @DisplayName("All fields should be null in newly created instance")
    void nullFields_onNewInstance() {
        ProjectReport r = new ProjectReport();

        assertThat(r.getProjectId()).isNull();
        assertThat(r.getProjectCode()).isNull();
        assertThat(r.getProjectTitle()).isNull();
        assertThat(r.getProjectStatus()).isNull();
        assertThat(r.getGroupName()).isNull();
        assertThat(r.getLineName()).isNull();
        assertThat(r.getResponsibleName()).isNull();
        assertThat(r.getCallTitle()).isNull();
        assertThat(r.getBudget()).isNull();
        assertThat(r.getStartDate()).isNull();
        assertThat(r.getEndDate()).isNull();
        assertThat(r.getCreatedAt()).isNull();
    }
}
