package com.sgi.fiis.reports.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ProgressReport}.
 * Verifies that the default constructor and all getters/setters work correctly.
 */
@DisplayName("ProgressReport - Unit Tests")
class ProgressReportTest {

    @Test
    @DisplayName("Default constructor should create empty instance without errors")
    void constructor_createsEmptyInstance() {
        ProgressReport r = new ProgressReport();
        assertThat(r).isNotNull();
    }

    @Test
    @DisplayName("Getters and setters should work correctly for all fields")
    void gettersAndSetters_workCorrectly() {
        ProgressReport r = new ProgressReport();

        LocalDateTime registered = LocalDateTime.of(2026, Month.APRIL, 25, 11, 45);

        r.setReportId(400);
        r.setProjectCode("PRJ-45");
        r.setProjectTitle("Estudio de Suelos");
        r.setReportType("INFORME_FINAL");
        r.setPeriod("2026-I");
        r.setProgressPercentage(new BigDecimal("100.00"));
        r.setReportStatus("APROBADO");
        r.setGroupName("Grupo Geotecnia");
        r.setRegisteredAt(registered);

        assertThat(r.getReportId()).isEqualTo(400);
        assertThat(r.getProjectCode()).isEqualTo("PRJ-45");
        assertThat(r.getProjectTitle()).isEqualTo("Estudio de Suelos");
        assertThat(r.getReportType()).isEqualTo("INFORME_FINAL");
        assertThat(r.getPeriod()).isEqualTo("2026-I");
        assertThat(r.getProgressPercentage()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(r.getReportStatus()).isEqualTo("APROBADO");
        assertThat(r.getGroupName()).isEqualTo("Grupo Geotecnia");
        assertThat(r.getRegisteredAt()).isEqualTo(registered);
    }

    @Test
    @DisplayName("All fields should be null in newly created instance")
    void nullFields_onNewInstance() {
        ProgressReport r = new ProgressReport();

        assertThat(r.getReportId()).isNull();
        assertThat(r.getProjectCode()).isNull();
        assertThat(r.getProjectTitle()).isNull();
        assertThat(r.getReportType()).isNull();
        assertThat(r.getPeriod()).isNull();
        assertThat(r.getProgressPercentage()).isNull();
        assertThat(r.getReportStatus()).isNull();
        assertThat(r.getGroupName()).isNull();
        assertThat(r.getRegisteredAt()).isNull();
    }
}
