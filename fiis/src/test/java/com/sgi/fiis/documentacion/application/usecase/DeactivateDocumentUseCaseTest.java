package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
class DeactivateDocumentUseCaseTest {

    private DocumentRepositoryPort documentRepositoryPort;
    private DeactivateDocumentUseCase deactivateDocumentUseCase;

    @BeforeEach
    void setUp() {
        this.documentRepositoryPort = mock(DocumentRepositoryPort.class);
        this.deactivateDocumentUseCase = new DeactivateDocumentUseCase(documentRepositoryPort);
    }

    @Test
    @DisplayName("RN-10 / RF-100: Desactivación lógica exitosa efectuada por el propietario del documento")
    void deactivateDocument_AsOwner_Success() {
        // Arrange
        Long docId = 1L;
        Long ownerId = 42L;
        String role = "DOCENTE_INVESTIGADOR";

        // CORRECCIÓN: Se añade .extension("PDF") para que el dominio no lance excepción
        Document document = Document.builder()
                .id(docId)
                .originalName("tesis.pdf")
                .storagePath("/path/tesis.pdf")
                .extension("PDF") 
                .sizeBytes(1024L)
                .uploadedById(ownerId)
                .active(true)
                .build();

        when(documentRepositoryPort.findById(docId)).thenReturn(Optional.of(document));

        // Act
        deactivateDocumentUseCase.execute(docId, ownerId, role);

        // Assert
        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepositoryPort, times(1)).save(documentCaptor.capture());
        
        Document savedDoc = documentCaptor.getValue();
        assertFalse(savedDoc.isActive()); 
    }

    @Test
    @DisplayName("RF-100: Desactivación lógica exitosa efectuada por un Administrador del sistema")
    void deactivateDocument_AsAdmin_Success() {
        // Arrange
        Long docId = 5L;
        Long ownerId = 12L;
        Long adminId = 99L;
        String role = "ADMIN";

        // CORRECCIÓN: Se añade .extension("PDF")
        Document document = Document.builder()
                .id(docId)
                .originalName("documento_admin.pdf")
                .storagePath("/path/admin.pdf")
                .extension("PDF")
                .sizeBytes(2048L)
                .uploadedById(ownerId)
                .active(true)
                .build();

        when(documentRepositoryPort.findById(docId)).thenReturn(Optional.of(document));

        // Act
        deactivateDocumentUseCase.execute(docId, adminId, role);

        // Assert
        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepositoryPort, times(1)).save(documentCaptor.capture());
        assertFalse(documentCaptor.getValue().isActive());
    }

    @Test
    @DisplayName("RNF-03: Intento de desactivación denegado para usuarios no autorizados")
    void deactivateDocument_UnauthorizedUser_ThrowsException() {
        // Arrange
        Long docId = 1L;
        Long ownerId = 42L;
        Long strangerId = 88L;
        String role = "ESTUDIANTE";

        // CORRECCIÓN: Se añade .extension("DOCX")
        Document document = Document.builder()
                .id(docId)
                .originalName("proyecto_estudiante.docx")
                .storagePath("/path/proyecto.docx")
                .extension("DOCX")
                .sizeBytes(512L)
                .uploadedById(ownerId)
                .active(true)
                .build();

        when(documentRepositoryPort.findById(docId)).thenReturn(Optional.of(document));

        // Act & Assert
        assertThrows(DocumentAccessDeniedException.class,
                () -> deactivateDocumentUseCase.execute(docId, strangerId, role));

        verify(documentRepositoryPort, never()).save(any(Document.class));
    }

    @Test
    @DisplayName("Exception: Error controlado si se intenta dar de baja un ID no registrado en la FIIS")
    void deactivateDocument_NotFound_ThrowsException() {
        // Arrange
        Long nonExistentId = 999L;
        when(documentRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DocumentNotFoundException.class,
                () -> deactivateDocumentUseCase.execute(nonExistentId, 1L, "ADMIN"));
    }
}