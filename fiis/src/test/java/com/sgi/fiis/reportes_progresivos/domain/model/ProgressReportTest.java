package com.sgi.fiis.reportes_progresivos.domain.model;

import com.sgi.fiis.reportes_progresivos.ProgressReportTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ProgressReport domain entity.
 * Validates state machine transitions, percentage validation, and document attachment.
 * All tests are flat at class-level to ensure static analyzers detect tests properly.
 */
@DisplayName("ProgressReport Domain Unit Tests")
class ProgressReportTest {

    private ProgressReport buildDefaultReport() {
        return ProgressReportTestHelper.createReport(1L, 10L, ProgressReportStatus.PENDING);
    }

    private ProgressReport buildReportInReview() {
        return ProgressReportTestHelper.createReport(1L, 10L, ProgressReportStatus.UNDER_REVIEW);
    }

    private ProgressReport buildObservedReport() {
        return ProgressReportTestHelper.createReport(1L, 10L, ProgressReportStatus.OBSERVED);
    }

    // =========================================================================
    // Construction Tests
    // =========================================================================

    @Test
    @DisplayName("New report starts in PENDING status")
    void newReportStartsInPending() {
        ProgressReport report = buildDefaultReport();
        assertEquals(ProgressReportStatus.PENDING, report.getReportStatus());
        assertNotNull(report.getRegistrationDate());
        assertNotNull(report.getLastUpdatedDate());
    }

    @Test
    @DisplayName("Constructor sets all fields correctly")
    void constructorSetsAllFields() {
        ProgressReport report = buildDefaultReport();
        assertEquals(10L, report.getProjectId());
        assertEquals(ProgressReportType.PARTIAL, report.getReportType());
        assertEquals("2026-I", report.getPeriod());
        assertEquals(new BigDecimal("50.00"), report.getProgressPercentage());
        assertEquals("Achievements", report.getAchievements());
        assertEquals("Difficulties", report.getDifficulties());
        assertEquals("Recommendations", report.getRecommendations());
    }

    // =========================================================================
    // Percentage Validation Tests
    // =========================================================================

    @Test
    @DisplayName("Null percentage throws IllegalArgumentException")
    void nullPercentageThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new ProgressReport(1L, ProgressReportType.PARTIAL, "2026-I",
                        null, "a", "b", "c"));
    }

    @Test
    @DisplayName("Negative percentage throws IllegalArgumentException")
    void negativePercentageThrows() {
        BigDecimal percentage = new BigDecimal("-1");
        assertThrows(IllegalArgumentException.class, () ->
                new ProgressReport(1L, ProgressReportType.PARTIAL, "2026-I",
                        percentage, "a", "b", "c"));
    }

    @Test
    @DisplayName("Percentage over 100 throws IllegalArgumentException")
    void over100PercentageThrows() {
        BigDecimal percentage = new BigDecimal("100.01");
        assertThrows(IllegalArgumentException.class, () ->
                new ProgressReport(1L, ProgressReportType.PARTIAL, "2026-I",
                        percentage, "a", "b", "c"));
    }

    @Test
    @DisplayName("Boundary value 0 is accepted")
    void zeroPercentageAccepted() {
        ProgressReport report = new ProgressReport(1L, ProgressReportType.PARTIAL, "2026-I",
                BigDecimal.ZERO, "a", "b", "c");
        assertEquals(BigDecimal.ZERO, report.getProgressPercentage());
    }

    @Test
    @DisplayName("Boundary value 100 is accepted")
    void hundredPercentageAccepted() {
        ProgressReport report = new ProgressReport(1L, ProgressReportType.PARTIAL, "2026-I",
                new BigDecimal("100.00"), "a", "b", "c");
        assertEquals(new BigDecimal("100.00"), report.getProgressPercentage());
    }

    @Test
    @DisplayName("Setter also validates percentage")
    void setterValidatesPercentage() {
        ProgressReport report = buildDefaultReport();
        BigDecimal percentage = new BigDecimal("101");
        assertThrows(IllegalArgumentException.class, () ->
                report.setProgressPercentage(percentage));
    }

    // =========================================================================
    // Submit For Review Tests
    // =========================================================================

    @Test
    @DisplayName("Submit transitions PENDING to UNDER_REVIEW")
    void submitChangesStatus() {
        ProgressReport report = buildDefaultReport();
        report.submitForReview();
        assertEquals(ProgressReportStatus.UNDER_REVIEW, report.getReportStatus());
    }

    @Test
    @DisplayName("Submit from UNDER_REVIEW throws IllegalStateException")
    void submitFromReviewThrows() {
        ProgressReport report = buildReportInReview();
        assertThrows(IllegalStateException.class, report::submitForReview);
    }

    // =========================================================================
    // Forward To Director Tests
    // =========================================================================

    @Test
    @DisplayName("Forward keeps status UNDER_REVIEW and updates timestamp")
    void forwardKeepsStatus() {
        ProgressReport report = buildReportInReview();
        var before = report.getLastUpdatedDate();
        report.forwardToDirector();
        assertEquals(ProgressReportStatus.UNDER_REVIEW, report.getReportStatus());
        assertTrue(report.getLastUpdatedDate().compareTo(before) >= 0);
    }

    @Test
    @DisplayName("Forward from PENDING transitions to UNDER_REVIEW and updates timestamp")
    void forwardFromPendienteTransitionsToUnderReview() {
        ProgressReport report = buildDefaultReport();
        var before = report.getLastUpdatedDate();
        report.forwardToDirector();
        assertEquals(ProgressReportStatus.UNDER_REVIEW, report.getReportStatus());
        assertTrue(report.getLastUpdatedDate().compareTo(before) >= 0);
    }

    @Test
    @DisplayName("Forward from OBSERVED throws IllegalStateException")
    void forwardFromObservadoThrows() {
        ProgressReport report = buildObservedReport();
        assertThrows(IllegalStateException.class, report::forwardToDirector);
    }

    // =========================================================================
    // Approve Tests
    // =========================================================================

    @Test
    @DisplayName("Approve transitions UNDER_REVIEW to APPROVED")
    void approveChangesStatus() {
        ProgressReport report = buildReportInReview();
        report.approve();
        assertEquals(ProgressReportStatus.APPROVED, report.getReportStatus());
    }

    @Test
    @DisplayName("Approve from PENDING throws IllegalStateException")
    void approveFromPendienteThrows() {
        ProgressReport report = buildDefaultReport();
        assertThrows(IllegalStateException.class, report::approve);
    }

    @Test
    @DisplayName("Approve from OBSERVED throws IllegalStateException")
    void approveFromObservadoThrows() {
        ProgressReport report = buildObservedReport();
        assertThrows(IllegalStateException.class, report::approve);
    }

    // =========================================================================
    // Observe Tests
    // =========================================================================

    @Test
    @DisplayName("Observe transitions UNDER_REVIEW to OBSERVED")
    void observeChangesStatus() {
        ProgressReport report = buildReportInReview();
        report.observe("Corregir tabla 3");
        assertEquals(ProgressReportStatus.OBSERVED, report.getReportStatus());
        assertEquals("Corregir tabla 3", report.getObservation());
    }

    @Test
    @DisplayName("Observe stores observation text")
    void observeStoresObservationText() {
        ProgressReport report = buildReportInReview();
        report.observe("Revisar metodología");
        assertEquals("Revisar metodología", report.getObservation());
    }

    @Test
    @DisplayName("Observe from PENDING throws IllegalStateException")
    void observeFromPendienteThrows() {
        ProgressReport report = buildDefaultReport();
        assertThrows(IllegalStateException.class, () -> report.observe("text"));
    }

    // =========================================================================
    // Reject Tests
    // =========================================================================

    @Test
    @DisplayName("Reject transitions UNDER_REVIEW to REJECTED")
    void rejectChangesStatus() {
        ProgressReport report = buildReportInReview();
        report.reject();
        assertEquals(ProgressReportStatus.REJECTED, report.getReportStatus());
    }

    @Test
    @DisplayName("Reject from OBSERVED throws IllegalStateException")
    void rejectFromObservadoThrows() {
        ProgressReport report = buildObservedReport();
        assertThrows(IllegalStateException.class, report::reject);
    }

    // =========================================================================
    // Amend Tests
    // =========================================================================

    @Test
    @DisplayName("Amend transitions OBSERVED to UNDER_REVIEW")
    void amendChangesStatus() {
        ProgressReport report = buildObservedReport();
        report.amend();
        assertEquals(ProgressReportStatus.UNDER_REVIEW, report.getReportStatus());
    }

    @Test
    @DisplayName("Amend from UNDER_REVIEW throws IllegalStateException")
    void amendFromReviewThrows() {
        ProgressReport report = buildReportInReview();
        assertThrows(IllegalStateException.class, report::amend);
    }

    @Test
    @DisplayName("Amend from PENDING throws IllegalStateException")
    void amendFromPendienteThrows() {
        ProgressReport report = buildDefaultReport();
        assertThrows(IllegalStateException.class, report::amend);
    }

    // =========================================================================
    // Document Attachment Tests
    // =========================================================================

    @Test
    @DisplayName("Attach document sets the ID and updates timestamp")
    void attachDocumentSetsId() {
        ProgressReport report = buildDefaultReport();
        report.attachDocument(42L);
        assertEquals(42L, report.getAttachedDocumentId());
        assertNotNull(report.getLastUpdatedDate());
    }

    @Test
    @DisplayName("Attach null document throws IllegalArgumentException")
    void attachNullDocumentThrows() {
        ProgressReport report = buildDefaultReport();
        assertThrows(IllegalArgumentException.class, () ->
                report.attachDocument(null));
    }
}
