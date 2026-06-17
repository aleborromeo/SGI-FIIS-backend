package com.sgi.fiis.documentacion.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Dominio - Document")
class DocumentTest {

    @Test
    @DisplayName("Debe validar la construcción por Builder, getters, setters y borrado lógico (RN-10)")
    void testDocumentDomainOperations() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Probar el patrón Builder Estático real de tu código
        Document doc1 = Document.builder()
                .id(1L)
                .originalName("tesis_sistemas.pdf")
                .storagePath("/uploads/tesis_sistemas.pdf")
                .extension("PDF")
                .sizeBytes(1024L)
                .uploadedById(42L)
                .uploadDate(now)
                .active(true)
                .build();

        // Verificar Getters
        assertEquals(1L, doc1.getId());
        assertEquals("tesis_sistemas.pdf", doc1.getOriginalName());
        assertEquals("/uploads/tesis_sistemas.pdf", doc1.getStoragePath());
        assertEquals("PDF", doc1.getExtension());
        assertEquals(1024L, doc1.getSizeBytes());
        assertEquals(42L, doc1.getUploadedById());
        assertEquals(now, doc1.getUploadDate());
        assertTrue(doc1.isActive());

        // 2. Probar Getters y Setters estándar del constructor vacío
        Document doc2 = new Document();
        doc2.setId(2L);
        doc2.setOriginalName("anexo.docx");
        doc2.setStoragePath("/uploads/anexo.docx");
        doc2.setExtension("DOCX");
        doc2.setSizeBytes(512L);
        doc2.setUploadedById(10L);
        doc2.setUploadDate(now);
        doc2.setActive(true);

        assertEquals(2L, doc2.getId());
        assertEquals("DOCX", doc2.getExtension());

        // 3. Probar la lógica de negocio del Borrado Lógico (RN-10)
        // Esto cubre la rama condicional que JaCoCo marcaba en rojo
        doc1.deactivate();
        assertFalse(doc1.isActive(), "El documento debería pasar a estar inactivo (false)");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si la extensión no es PDF, DOC o DOCX")
    void testDocumentInvalidExtension() {
        // Forzamos el lanzamiento de la excepción para cubrir las ramas del IF de validación
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Document.builder()
                    .extension("EXE") // Extensión inválida
                    .build();
        });

        assertEquals("Extensión de archivo no permitida. Solo se admite PDF, DOC o DOCX.", exception.getMessage());
    }
}