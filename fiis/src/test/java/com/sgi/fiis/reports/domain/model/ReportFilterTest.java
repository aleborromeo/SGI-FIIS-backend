package com.sgi.fiis.reports.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ReportFilter}.
 * Verifies default values, range validations, and offset calculation.
 */
@DisplayName("ReportFilter - Unit Tests")
class ReportFilterTest {

    // -------------------------------------------------------------------------
    // Default Values
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Constructor without args should initialize page=0 and size=20")
    void constructor_shouldHaveDefaultValues() {
        ReportFilter f = new ReportFilter();

        assertThat(f.getPage()).isZero();
        assertThat(f.getSize()).isEqualTo(20);
        assertThat(f.getOffset()).isZero();
    }

    @Test
    @DisplayName("All optional fields should be null by default")
    void constructor_optionalFieldsShouldBeNull() {
        ReportFilter f = new ReportFilter();

        assertThat(f.getGroupId()).isNull();
        assertThat(f.getStatus()).isNull();
        assertThat(f.getFromDate()).isNull();
        assertThat(f.getToDate()).isNull();
        assertThat(f.getResearcherId()).isNull();
        assertThat(f.getCallId()).isNull();
        assertThat(f.getProcedureType()).isNull();
    }

    // -------------------------------------------------------------------------
    // Page Validation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("setPage with negative value should set page to 0")
    void setPage_negativeValue_setsToZero() {
        ReportFilter f = new ReportFilter();
        f.setPage(-5);
        assertThat(f.getPage()).isZero();
    }

    @Test
    @DisplayName("setPage with positive value should assign it correctly")
    void setPage_positiveValue_assignsCorrectly() {
        ReportFilter f = new ReportFilter();
        f.setPage(3);
        assertThat(f.getPage()).isEqualTo(3);
    }

    @Test
    @DisplayName("setPage with zero should assign it correctly")
    void setPage_withZero_assignsCorrectly() {
        ReportFilter f = new ReportFilter();
        f.setPage(0);
        assertThat(f.getPage()).isZero();
    }

    // -------------------------------------------------------------------------
    // Size Validation
    // -------------------------------------------------------------------------

    static Stream<Integer> invalidSizes() {
        return Stream.of(0, -1, -10, 101, 200, Integer.MAX_VALUE);
    }

    @ParameterizedTest(name = "setSize({0}) should fallback to default 20")
    @MethodSource("invalidSizes")
    @DisplayName("setSize with invalid value should use default 20")
    void setSize_invalidValue_usesDefault(int invalidSize) {
        ReportFilter f = new ReportFilter();
        f.setSize(invalidSize);
        assertThat(f.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("setSize with valid value (1-100) should assign it correctly")
    void setSize_validValue_assignsCorrectly() {
        ReportFilter f = new ReportFilter();
        f.setSize(50);
        assertThat(f.getSize()).isEqualTo(50);
    }

    @Test
    @DisplayName("setSize with maximum value 100 should assign it correctly")
    void setSize_with100_assignsCorrectly() {
        ReportFilter f = new ReportFilter();
        f.setSize(100);
        assertThat(f.getSize()).isEqualTo(100);
    }

    // -------------------------------------------------------------------------
    // Offset Calculation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("getOffset should return page * size correctly")
    void getOffset_calculatesCorrectly() {
        ReportFilter f = new ReportFilter();
        f.setPage(2);
        f.setSize(10);
        assertThat(f.getOffset()).isEqualTo(20);
    }

    @Test
    @DisplayName("getOffset on page 0 should be 0")
    void getOffset_onPageZero_shouldBeZero() {
        ReportFilter f = new ReportFilter();
        f.setPage(0);
        f.setSize(15);
        assertThat(f.getOffset()).isZero();
    }

    // -------------------------------------------------------------------------
    // Optional Fields Setters & Getters
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Getters and setters of optional fields should work correctly")
    void optionalFields_gettersAndSettersWork() {
        ReportFilter f = new ReportFilter();
        LocalDate from = LocalDate.of(2024, Month.JANUARY, 1);
        LocalDate to = LocalDate.of(2024, Month.DECEMBER, 31);

        f.setGroupId(1);
        f.setStatus("APROBADO");
        f.setFromDate(from);
        f.setToDate(to);
        f.setResearcherId(5);
        f.setCallId(2);
        f.setProcedureType("PROYECTO");

        assertThat(f.getGroupId()).isEqualTo(1);
        assertThat(f.getStatus()).isEqualTo("APROBADO");
        assertThat(f.getFromDate()).isEqualTo(from);
        assertThat(f.getToDate()).isEqualTo(to);
        assertThat(f.getResearcherId()).isEqualTo(5);
        assertThat(f.getCallId()).isEqualTo(2);
        assertThat(f.getProcedureType()).isEqualTo("PROYECTO");
    }
}
