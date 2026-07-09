package com.sgi.fiis.reports.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ResolutionReport}.
 * Verifies that the default constructor and all getters/setters work correctly.
 */
@DisplayName("ResolutionReport - Unit Tests")
class ResolutionReportTest {

    @Test
    @DisplayName("Default constructor should create empty instance without errors")
    void constructor_createsEmptyInstance() {
        ResolutionReport r = new ResolutionReport();
        assertThat(r).isNotNull();
    }

    @Test
    @DisplayName("Getters and setters should work correctly for all fields")
    void gettersAndSetters_workCorrectly() {
        ResolutionReport r = new ResolutionReport();

        LocalDate issue = LocalDate.of(2026, Month.MARCH, 15);
        LocalDateTime registered = LocalDateTime.of(2026, Month.MARCH, 16, 8, 30);

        r.setResolutionId(300);
        r.setResolutionNumber("RES-045-2026");
        r.setIssueDate(issue);
        r.setSubject("Aprobacion de Proyecto");
        r.setProcedureCode("TRM-105");
        r.setProcedureType("PROYECTO");
        r.setApplicantName("Carlos Ruiz");
        r.setRegisteredAt(registered);

        assertThat(r.getResolutionId()).isEqualTo(300);
        assertThat(r.getResolutionNumber()).isEqualTo("RES-045-2026");
        assertThat(r.getIssueDate()).isEqualTo(issue);
        assertThat(r.getSubject()).isEqualTo("Aprobacion de Proyecto");
        assertThat(r.getProcedureCode()).isEqualTo("TRM-105");
        assertThat(r.getProcedureType()).isEqualTo("PROYECTO");
        assertThat(r.getApplicantName()).isEqualTo("Carlos Ruiz");
        assertThat(r.getRegisteredAt()).isEqualTo(registered);
    }

    @Test
    @DisplayName("All fields should be null in newly created instance")
    void nullFields_onNewInstance() {
        ResolutionReport r = new ResolutionReport();

        assertThat(r.getResolutionId()).isNull();
        assertThat(r.getResolutionNumber()).isNull();
        assertThat(r.getIssueDate()).isNull();
        assertThat(r.getSubject()).isNull();
        assertThat(r.getProcedureCode()).isNull();
        assertThat(r.getProcedureType()).isNull();
        assertThat(r.getApplicantName()).isNull();
        assertThat(r.getRegisteredAt()).isNull();
    }
}
