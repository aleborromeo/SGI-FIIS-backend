package com.sgi.fiis.convocatorias.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "convocatorias")
public class ResearchCallEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_convocatoria")
    private Integer id;

    @Column(name = "titulo_convocatoria", nullable = false, length = 150)
    private String title;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate startDate;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate endDate;

    @Column(name = "estado", nullable = false, length = 20)
    private String status;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ResearchCallEntity() {}

    public ResearchCallEntity(Integer id, String title, LocalDate startDate, LocalDate endDate, String status, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
 
        }
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static ResearchCallEntityBuilder builder() {
        return new ResearchCallEntityBuilder();
    }

    public static class ResearchCallEntityBuilder {
        private Integer id;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;

        public ResearchCallEntityBuilder id(Integer id) { this.id = id; return this; }
        public ResearchCallEntityBuilder title(String title) { this.title = title; return this; }
        public ResearchCallEntityBuilder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public ResearchCallEntityBuilder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public ResearchCallEntityBuilder status(String status) { this.status = status; return this; }

        public ResearchCallEntity build() {
            return new ResearchCallEntity(id, title, startDate, endDate, status, null);
        }
    }
}
