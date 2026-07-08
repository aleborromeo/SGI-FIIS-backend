package com.sgi.fiis.shared.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DocumentEntity Unit Tests")
class DocumentEntityTest {

    @Test
    @DisplayName("Should create entity using no-args constructor")
    void testNoArgsConstructor() {
        DocumentEntity entity = new DocumentEntity();
        assertNull(entity.getId());
    }

    @Test
    @DisplayName("Should create entity using all-args constructor")
    void testAllArgsConstructor() {
        DocumentEntity entity = new DocumentEntity(
                1, "report.pdf", "/uploads/report.pdf",
                2048L, "pdf", 10L, null
        );

        assertEquals(1, entity.getId());
        assertEquals("report.pdf", entity.getOriginalName());
        assertEquals("/uploads/report.pdf", entity.getStoragePath());
        assertEquals(2048L, entity.getSizeBytes());
        assertEquals("pdf", entity.getFileExtension());
        assertEquals(10L, entity.getCreatorId());
        assertNull(entity.getCreatedAt());
    }

    @Test
    @DisplayName("Should create entity using builder")
    void testBuilder() {
        DocumentEntity entity = DocumentEntity.builder()
                .id(2)
                .originalName("thesis.docx")
                .storagePath("/uploads/thesis.docx")
                .sizeBytes(102400L)
                .fileExtension("docx")
                .creatorId(5L)
                .build();

        assertEquals(2, entity.getId());
        assertEquals("thesis.docx", entity.getOriginalName());
        assertEquals("/uploads/thesis.docx", entity.getStoragePath());
        assertEquals(102400L, entity.getSizeBytes());
        assertEquals("docx", entity.getFileExtension());
        assertEquals(5L, entity.getCreatorId());
        assertNull(entity.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get all fields via setters")
    void testSettersAndGetters() {
        DocumentEntity entity = new DocumentEntity();

        entity.setId(3);
        entity.setOriginalName("image.png");
        entity.setStoragePath("/uploads/image.png");
        entity.setSizeBytes(5120L);
        entity.setFileExtension("png");
        entity.setCreatorId(20L);

        assertEquals(3, entity.getId());
        assertEquals("image.png", entity.getOriginalName());
        assertEquals("/uploads/image.png", entity.getStoragePath());
        assertEquals(5120L, entity.getSizeBytes());
        assertEquals("png", entity.getFileExtension());
        assertEquals(20L, entity.getCreatorId());
    }

    @Test
    @DisplayName("Should set createdAt on PrePersist")
    void testOnCreate() {
        DocumentEntity entity = new DocumentEntity();
        assertNull(entity.getCreatedAt());

        entity.onCreate();

        assertNotNull(entity.getCreatedAt());
        assertTrue(entity.getCreatedAt() instanceof LocalDateTime);
    }
}
