package com.sgi.fiis.convocatorias.application.dto;

import java.time.LocalDate;
import java.util.List;

public class UpdateCallRequest {

    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer documentId;
    private String poblacionObjetivo;
    private List<Integer> researchLineIds;

    public UpdateCallRequest() {
    }

    public UpdateCallRequest(String title, String description, LocalDate startDate, LocalDate endDate,
                             Integer documentId, String poblacionObjetivo, List<Integer> researchLineIds) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.documentId = documentId;
        this.poblacionObjetivo = poblacionObjetivo;
        // skipcq: JAVA-E1086
        this.researchLineIds = researchLineIds == null ? null : List.copyOf(researchLineIds);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public String getPoblacionObjetivo() {
        return poblacionObjetivo;
    }

    public void setPoblacionObjetivo(String poblacionObjetivo) {
        this.poblacionObjetivo = poblacionObjetivo;
    }

    public List<Integer> getResearchLineIds() {
        return researchLineIds == null ? null : List.copyOf(researchLineIds);
    }

    public void setResearchLineIds(List<Integer> researchLineIds) {
        // skipcq: JAVA-E1086
        this.researchLineIds = researchLineIds == null ? null : List.copyOf(researchLineIds);
    }

    public static UpdateCallRequestBuilder builder() {
        return new UpdateCallRequestBuilder();
    }

    public static class UpdateCallRequestBuilder {
        private String title;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer documentId;
        private String poblacionObjetivo;
        private List<Integer> researchLineIds;

        public UpdateCallRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public UpdateCallRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public UpdateCallRequestBuilder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public UpdateCallRequestBuilder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public UpdateCallRequestBuilder documentId(Integer documentId) {
            this.documentId = documentId;
            return this;
        }

        public UpdateCallRequestBuilder poblacionObjetivo(String poblacionObjetivo) {
            this.poblacionObjetivo = poblacionObjetivo;
            return this;
        }

        public UpdateCallRequestBuilder researchLineIds(List<Integer> researchLineIds) {
            // skipcq: JAVA-E1086
            this.researchLineIds = researchLineIds == null ? null : List.copyOf(researchLineIds);
            return this;
        }

        public UpdateCallRequest build() {
            return new UpdateCallRequest(title, description, startDate, endDate, documentId, poblacionObjetivo, researchLineIds);
        }
    }
}
