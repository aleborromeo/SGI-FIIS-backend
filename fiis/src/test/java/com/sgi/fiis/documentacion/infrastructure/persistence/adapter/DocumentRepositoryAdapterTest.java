package com.sgi.fiis.documentacion.infrastructure.persistence.adapter;

import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.infrastructure.persistence.entity.DocumentEntity;
import com.sgi.fiis.documentacion.infrastructure.persistence.repository.JpaDocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DocumentRepositoryAdapterTest {

    private JpaDocumentRepository jpaDocumentRepository;
    private DocumentRepositoryAdapter documentRepositoryAdapter;

    @BeforeEach
    void setUp() {
        this.jpaDocumentRepository = mock(JpaDocumentRepository.class);
        this.documentRepositoryAdapter = new DocumentRepositoryAdapter(jpaDocumentRepository);
    }

    @Test
    @DisplayName("Infrastructure Mappings: Mapeo correcto de Dominio a Entidad JPA al guardar")
    void save_MapsCorrectlyToEntity() {
        // Arrange
        Document domainDoc = Document.builder()
                .id(null)
                .originalName("informe_avance.docx")
                .storagePath("/local/storage/file.docx")
                .extension("DOCX")
                .sizeBytes(2048L)
                .uploadedById(15L)
                .active(true)
                .build();

        DocumentEntity savedEntity = new DocumentEntity();
        savedEntity.setId(100L);
        savedEntity.setOriginalName("informe_avance.docx");
        savedEntity.setStoragePath("/local/storage/file.docx");
        savedEntity.setExtension("DOCX");
        savedEntity.setSizeBytes(2048L);
        savedEntity.setUploadedById(15L);
        savedEntity.setUploadDate(LocalDateTime.of(2026, java.time.Month.JUNE, 17, 10, 0));
        savedEntity.setActive(true);

        when(jpaDocumentRepository.save(any(DocumentEntity.class))).thenReturn(savedEntity);

        // Act
        Document resultDomain = documentRepositoryAdapter.save(domainDoc);

        // Assert
        ArgumentCaptor<DocumentEntity> entityCaptor = ArgumentCaptor.forClass(DocumentEntity.class);
        verify(jpaDocumentRepository, times(1)).save(entityCaptor.capture());

        DocumentEntity capturedEntity = entityCaptor.getValue();
        assertEquals("informe_avance.docx", capturedEntity.getOriginalName());
        assertEquals("DOCX", capturedEntity.getExtension());
        assertEquals(15L, capturedEntity.getUploadedById());
        
        assertNotNull(resultDomain);
        assertEquals(100L, resultDomain.getId());
    }

    @Test
    @DisplayName("Infrastructure Mappings: Mapeo correcto de Entidad JPA a Dominio al buscar por ID")
    void findById_MapsCorrectlyToDomain() {
        // Arrange
        Long searchId = 100L;
        DocumentEntity entity = new DocumentEntity();
        entity.setId(searchId);
        entity.setOriginalName("convocatoria_anexo.pdf");
        entity.setStoragePath("/local/anexo.pdf");
        entity.setExtension("PDF");
        entity.setSizeBytes(5000L);
        entity.setUploadedById(1L);
        entity.setUploadDate(LocalDateTime.of(2026, java.time.Month.JUNE, 17, 10, 0));
        entity.setActive(true);

        when(jpaDocumentRepository.findById(searchId)).thenReturn(Optional.of(entity));

        // Act
        Optional<Document> result = documentRepositoryAdapter.findById(searchId);

        // Assert
        assertTrue(result.isPresent());
        Document domain = result.get();
        assertEquals(searchId, domain.getId());
        assertEquals("convocatoria_anexo.pdf", domain.getOriginalName());
        assertEquals("PDF", domain.getExtension());
        assertTrue(domain.isActive());
    }

    @Test
    @DisplayName("Infrastructure Mappings: findById retorna vacío cuando no existe")
    void findById_ReturnsEmptyWhenNotFound() {
        when(jpaDocumentRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Document> result = documentRepositoryAdapter.findById(999L);

        assertTrue(result.isEmpty());
        verify(jpaDocumentRepository).findById(999L);
    }

    @Test
    @DisplayName("Infrastructure Mappings: findAll mapea correctamente la lista de entidades a dominio")
    void findAll_MapsCorrectlyToDomainList() {
        DocumentEntity entity1 = new DocumentEntity();
        entity1.setId(1L);
        entity1.setOriginalName("doc1.pdf");
        entity1.setStoragePath("/path/doc1.pdf");
        entity1.setExtension("PDF");
        entity1.setSizeBytes(1000L);
        entity1.setUploadedById(1L);
        entity1.setUploadDate(LocalDateTime.of(2026, java.time.Month.JUNE, 17, 10, 0));
        entity1.setActive(true);

        DocumentEntity entity2 = new DocumentEntity();
        entity2.setId(2L);
        entity2.setOriginalName("doc2.docx");
        entity2.setStoragePath("/path/doc2.docx");
        entity2.setExtension("DOCX");
        entity2.setSizeBytes(2000L);
        entity2.setUploadedById(2L);
        entity2.setUploadDate(LocalDateTime.of(2026, java.time.Month.JUNE, 18, 10, 0));
        entity2.setActive(false);

        when(jpaDocumentRepository.findAll()).thenReturn(List.of(entity1, entity2));

        List<Document> result = documentRepositoryAdapter.findAll();

        assertEquals(2, result.size());
        assertEquals("doc1.pdf", result.get(0).getOriginalName());
        assertEquals("doc2.docx", result.get(1).getOriginalName());
        assertTrue(result.get(0).isActive());
        assertFalse(result.get(1).isActive());
        verify(jpaDocumentRepository).findAll();
    }

    @Test
    @DisplayName("Infrastructure Mappings: findAll retorna lista vacía cuando no hay documentos")
    void findAll_ReturnsEmptyList() {
        when(jpaDocumentRepository.findAll()).thenReturn(List.of());

        List<Document> result = documentRepositoryAdapter.findAll();

        assertTrue(result.isEmpty());
        verify(jpaDocumentRepository).findAll();
    }
}