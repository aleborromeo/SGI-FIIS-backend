package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter;

import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportType;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.entity.ProgressReportEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProgressReportMapper Unit Tests")
class ProgressReportMapperTest {

    @Test
    @DisplayName("Should map Entity to Domain model correctly")
    void shouldMapEntityToDomain() {
        LocalDateTime now = LocalDateTime.now();
        ProgressReportEntity entity = ProgressReportEntity.builder()
                .id(1L)
                .projectId(10L)
                .reportType("PARCIAL")
                .period("2026-I")
                .progressPercentage(new BigDecimal("45.50"))
                .achievements("logros")
                .difficulties("dificultades")
                .recommendations("recomendaciones")
                .attachedDocumentId(99L)
                .reportStatus("PENDIENTE")
                .registrationDate(now)
                .lastUpdatedDate(now)
                .build();

        ProgressReport domain = ProgressReportMapper.toDomain(entity);

        assertNotNull(domain);
        assertFields(domain, 1L, 10L, ProgressReportType.PARTIAL, "2026-I",
                new BigDecimal("45.50"), "logros", "dificultades", "recomendaciones", 99L, ProgressReportStatus.PENDING, now, now);
    }

    @Test
    @DisplayName("Should map Domain model to Entity correctly")
    void shouldMapDomainToEntity() {
        LocalDateTime now = LocalDateTime.now();
        ProgressReport domain = new ProgressReport(10L, ProgressReportType.FINAL, "2026-II",
                new BigDecimal("100.00"), "logros", "dificultades", "recomendaciones");
        domain.setId(1L);
        domain.setAttachedDocumentId(99L);
        domain.setReportStatus(ProgressReportStatus.APPROVED);
        domain.setRegistrationDate(now);
        domain.setLastUpdatedDate(now);

        ProgressReportEntity entity = ProgressReportMapper.toEntity(domain);

        assertNotNull(entity);
        assertEntityMatches(domain, entity);
        assertEquals("FINAL", entity.getReportType());
        assertEquals("APROBADO", entity.getReportStatus());
    }

    @Test
    @DisplayName("Should map Domain model to Response DTO correctly")
    void shouldMapDomainToResponse() {
        LocalDateTime now = LocalDateTime.now();
        ProgressReport domain = new ProgressReport(10L, ProgressReportType.PARTIAL, "2026-I",
                new BigDecimal("45.50"), "logros", "dificultades", "recomendaciones");
        domain.setId(1L);
        domain.setAttachedDocumentId(99L);
        domain.setReportStatus(ProgressReportStatus.PENDING);
        domain.setRegistrationDate(now);
        domain.setLastUpdatedDate(now);

        ProgressReportResponse response = ProgressReportMapper.toResponse(domain);

        assertNotNull(response);
        assertResponseMatches(domain, response);
    }

    private void assertFields(ProgressReport domain, Long id, Long projectId, ProgressReportType type, String period,
                              BigDecimal percentage, String achievements, String difficulties, String recommendations,
                              Long attachedDocId, ProgressReportStatus status, LocalDateTime regDate, LocalDateTime updDate) {
        assertEquals(id, domain.getId());
        assertEquals(projectId, domain.getProjectId());
        assertEquals(type, domain.getReportType());
        assertEquals(period, domain.getPeriod());
        assertEquals(percentage, domain.getProgressPercentage());
        assertEquals(achievements, domain.getAchievements());
        assertEquals(difficulties, domain.getDifficulties());
        assertEquals(recommendations, domain.getRecommendations());
        assertEquals(attachedDocId, domain.getAttachedDocumentId());
        assertEquals(status, domain.getReportStatus());
        assertEquals(regDate, domain.getRegistrationDate());
        assertEquals(updDate, domain.getLastUpdatedDate());
    }

    private void assertEntityMatches(ProgressReport domain, ProgressReportEntity entity) {
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getProjectId(), entity.getProjectId());
        assertEquals(domain.getPeriod(), entity.getPeriod());
        assertEquals(domain.getProgressPercentage(), entity.getProgressPercentage());
        assertEquals(domain.getAchievements(), entity.getAchievements());
        assertEquals(domain.getDifficulties(), entity.getDifficulties());
        assertEquals(domain.getRecommendations(), entity.getRecommendations());
        assertEquals(domain.getAttachedDocumentId(), entity.getAttachedDocumentId());
        assertEquals(domain.getRegistrationDate(), entity.getRegistrationDate());
        assertEquals(domain.getLastUpdatedDate(), entity.getLastUpdatedDate());
    }

    private void assertResponseMatches(ProgressReport domain, ProgressReportResponse response) {
        assertEquals(domain.getId(), response.getId());
        assertEquals(domain.getProjectId(), response.getProjectId());
        assertEquals(domain.getReportType(), response.getReportType());
        assertEquals(domain.getPeriod(), response.getPeriod());
        assertEquals(domain.getProgressPercentage(), response.getProgressPercentage());
        assertEquals(domain.getAchievements(), response.getAchievements());
        assertEquals(domain.getDifficulties(), response.getDifficulties());
        assertEquals(domain.getRecommendations(), response.getRecommendations());
        assertEquals(domain.getAttachedDocumentId(), response.getAttachedDocumentId());
        assertEquals(domain.getReportStatus(), response.getReportStatus());
        assertEquals(domain.getRegistrationDate(), response.getRegistrationDate());
        assertEquals(domain.getLastUpdatedDate(), response.getLastUpdatedDate());
    }

    @Test
    @DisplayName("Should verify constructor is private")
    void testConstructorIsPrivate() throws NoSuchMethodException {
        java.lang.reflect.Constructor<ProgressReportMapper> constructor = ProgressReportMapper.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        assertThrows(java.lang.reflect.InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    @DisplayName("Should map null values in type and status mapping functions")
    void shouldMapNullValues() {
        ProgressReportEntity entity = ProgressReportEntity.builder()
                .id(1L)
                .reportType(null)
                .reportStatus(null)
                .progressPercentage(java.math.BigDecimal.ZERO)
                .build();
        ProgressReport domain = ProgressReportMapper.toDomain(entity);
        assertNull(domain.getReportType());
        assertNull(domain.getReportStatus());

        ProgressReport domain2 = new ProgressReport();
        domain2.setReportType(null);
        domain2.setReportStatus(null);
        domain2.setProgressPercentage(java.math.BigDecimal.ZERO);
        ProgressReportEntity entity2 = ProgressReportMapper.toEntity(domain2);
        assertNull(entity2.getReportType());
        assertNull(entity2.getReportStatus());
    }


    @Test
    @DisplayName("Should throw exception for unknown report type string")
    void shouldThrowOnUnknownType() {
        ProgressReportEntity entity = ProgressReportEntity.builder()
                .reportType("UNKNOWN_TYPE")
                .build();
        assertThrows(IllegalArgumentException.class, () -> ProgressReportMapper.toDomain(entity));
    }

    @Test
    @DisplayName("Should throw exception for unknown report status string")
    void shouldThrowOnUnknownStatus() {
        ProgressReportEntity entity = ProgressReportEntity.builder()
                .reportStatus("UNKNOWN_STATUS")
                .build();
        assertThrows(IllegalArgumentException.class, () -> ProgressReportMapper.toDomain(entity));
    }
}

