package com.sgi.fiis.reports.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ProcedureReport}.
 * Verifies that the default constructor and all getters/setters work correctly.
 */
@DisplayName("ProcedureReport - Unit Tests")
class ProcedureReportTest {

    @Test
    @DisplayName("Default constructor should create empty instance without errors")
    void constructor_createsEmptyInstance() {
        ProcedureReport r = new ProcedureReport();
        assertThat(r).isNotNull();
    }

    @Test
    @DisplayName("Getters and setters should work correctly for all fields")
    void gettersAndSetters_workCorrectly() {
        ProcedureReport r = new ProcedureReport();

        LocalDateTime submit = LocalDateTime.of(2024, Month.MAY, 10, 9, 0);
        LocalDateTime update = LocalDateTime.of(2024, Month.MAY, 12, 15, 30);

        r.setProcedureId(5);
        r.setProcedureCode("TRM-2024-005");
        r.setProcedureType("PROYECTO");
        r.setApplicantName("Ana Torres");
        r.setCurrentStatus("EN_REVISION");
        r.setCurrentReviewerRole("DECANO");
        r.setGroupName("Grupo Beta");
        r.setSubmittedAt(submit);
        r.setUpdatedAt(update);

        assertThat(r.getProcedureId()).isEqualTo(5);
        assertThat(r.getProcedureCode()).isEqualTo("TRM-2024-005");
        assertThat(r.getProcedureType()).isEqualTo("PROYECTO");
        assertThat(r.getApplicantName()).isEqualTo("Ana Torres");
        assertThat(r.getCurrentStatus()).isEqualTo("EN_REVISION");
        assertThat(r.getCurrentReviewerRole()).isEqualTo("DECANO");
        assertThat(r.getGroupName()).isEqualTo("Grupo Beta");
        assertThat(r.getSubmittedAt()).isEqualTo(submit);
        assertThat(r.getUpdatedAt()).isEqualTo(update);
    }

    @Test
    @DisplayName("All fields should be null in newly created instance")
    void nullFields_onNewInstance() {
        ProcedureReport r = new ProcedureReport();

        assertThat(r.getProcedureId()).isNull();
        assertThat(r.getProcedureCode()).isNull();
        assertThat(r.getProcedureType()).isNull();
        assertThat(r.getApplicantName()).isNull();
        assertThat(r.getCurrentStatus()).isNull();
        assertThat(r.getCurrentReviewerRole()).isNull();
        assertThat(r.getGroupName()).isNull();
        assertThat(r.getSubmittedAt()).isNull();
        assertThat(r.getUpdatedAt()).isNull();
    }
}
