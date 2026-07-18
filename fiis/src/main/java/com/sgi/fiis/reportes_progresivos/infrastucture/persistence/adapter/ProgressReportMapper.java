package com.sgi.fiis.reportes_progresivos.infrastucture.persistence.adapter;

import com.sgi.fiis.reportes_progresivos.application.dto.ProgressReportResponse;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportStatus;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReport;
import com.sgi.fiis.reportes_progresivos.domain.model.ProgressReportType;
import com.sgi.fiis.reportes_progresivos.infrastucture.persistence.entity.ProgressReportEntity;

/**
 * Mapper for conversions between Domain model, JPA entity, and Response DTO.
 */
public final class ProgressReportMapper {

    private ProgressReportMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ProgressReport toDomain(ProgressReportEntity entity) {
        ProgressReport domain = new ProgressReport();
        domain.setId(entity.getId());
        domain.setProjectId(entity.getProjectId());
        domain.setReportType(mapTypeToDomain(entity.getReportType()));
        domain.setPeriod(entity.getPeriod());
        domain.setProgressPercentage(entity.getProgressPercentage());
        domain.setAchievements(entity.getAchievements());
        domain.setDifficulties(entity.getDifficulties());
        domain.setRecommendations(entity.getRecommendations());
        domain.setAttachedDocumentId(entity.getAttachedDocumentId());
        domain.setReportStatus(mapStatusToDomain(entity.getReportStatus()));
        domain.setRegistrationDate(entity.getRegistrationDate());
        domain.setLastUpdatedDate(entity.getLastUpdatedDate());
        return domain;
    }

    public static ProgressReportEntity toEntity(ProgressReport domain) {
        return ProgressReportEntity.builder()
                .id(domain.getId())
                .projectId(domain.getProjectId())
                .reportType(mapTypeToEntity(domain.getReportType()))
                .period(domain.getPeriod())
                .progressPercentage(domain.getProgressPercentage())
                .achievements(domain.getAchievements())
                .difficulties(domain.getDifficulties())
                .recommendations(domain.getRecommendations())
                .attachedDocumentId(domain.getAttachedDocumentId())
                .reportStatus(mapStatusToEntity(domain.getReportStatus()))
                .registrationDate(domain.getRegistrationDate())
                .lastUpdatedDate(domain.getLastUpdatedDate())
                .build();
    }

    public static ProgressReportResponse toResponse(ProgressReport domain) {
        ProgressReportResponse response = new ProgressReportResponse();
        response.setId(domain.getId());
        response.setProjectId(domain.getProjectId());
        response.setReportType(domain.getReportType());
        response.setPeriod(domain.getPeriod());
        response.setProgressPercentage(domain.getProgressPercentage());
        response.setAchievements(domain.getAchievements());
        response.setDifficulties(domain.getDifficulties());
        response.setRecommendations(domain.getRecommendations());
        response.setAttachedDocumentId(domain.getAttachedDocumentId());
        response.setReportStatus(domain.getReportStatus());
        response.setRegistrationDate(domain.getRegistrationDate());
        response.setLastUpdatedDate(domain.getLastUpdatedDate());
        return response;
    }

    private static String mapTypeToEntity(ProgressReportType type) {
        if (type == null) return null;
        return switch (type) {
            case PARTIAL -> "PARCIAL";
            case FINAL -> "FINAL";
        };
    }

    private static ProgressReportType mapTypeToDomain(String typeStr) {
        if (typeStr == null) return null;
        return switch (typeStr) {
            case "PARCIAL" -> ProgressReportType.PARTIAL;
            case "FINAL" -> ProgressReportType.FINAL;
            default -> throw new IllegalArgumentException("Unknown report type: " + typeStr);
        };
    }

    static String mapStatusToEntity(ProgressReportStatus status) {
        if (status == null) return null;
        return switch (status) {
            case PENDING -> "PENDIENTE";
            case UNDER_REVIEW -> "EN_REVISION";
            case APPROVED -> "APROBADO";
            case OBSERVED -> "OBSERVADO";
            case REJECTED -> "RECHAZADO";
        };
    }

    static ProgressReportStatus mapStatusToDomain(String statusStr) {
        if (statusStr == null) return null;
        return switch (statusStr) {
            case "PENDIENTE" -> ProgressReportStatus.PENDING;
            case "EN_REVISION" -> ProgressReportStatus.UNDER_REVIEW;
            case "APROBADO" -> ProgressReportStatus.APPROVED;
            case "OBSERVADO" -> ProgressReportStatus.OBSERVED;
            case "RECHAZADO" -> ProgressReportStatus.REJECTED;
            default -> throw new IllegalArgumentException("Unknown report status: " + statusStr);
        };
    }
}
