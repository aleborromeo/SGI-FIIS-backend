package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.application.dto.DocumentResponseDto;
import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;
import com.sgi.fiis.resolutions.domain.port.out.ResolutionRepositoryPort;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("all")
class DocumentUseCaseTest {

    private DocumentRepositoryPort documentRepositoryPort;
    private FileStoragePort fileStoragePort;
    private ResolutionRepositoryPort resolutionRepositoryPort;
    private ProcedureRepositoryPort procedureRepositoryPort;
    
    private UploadDocumentUseCase uploadDocumentUseCase;
    private DownloadDocumentUseCase downloadDocumentUseCase;

    @BeforeEach
    void setUp() {
        this.documentRepositoryPort = mock(DocumentRepositoryPort.class);
        this.fileStoragePort = mock(FileStoragePort.class);
        this.resolutionRepositoryPort = mock(ResolutionRepositoryPort.class);
        this.procedureRepositoryPort = mock(ProcedureRepositoryPort.class);

        this.uploadDocumentUseCase = new UploadDocumentUseCase(documentRepositoryPort, fileStoragePort);
        this.downloadDocumentUseCase = new DownloadDocumentUseCase(
                documentRepositoryPort, fileStoragePort,
                resolutionRepositoryPort, procedureRepositoryPort);
    }

    @Test
    @DisplayName("RF-65 / RNF-07: Carga exitosa de documento con extensión permitida (PDF)")
    void uploadDocument_Success() {
        // Arrange (Preparación)
        InputStream fakeStream = new ByteArrayInputStream("bytes-de-prueba".getBytes());
        String fileName = "tesis_sistema_fiis.pdf";
        Long sizeBytes = 1024L;
        Long userId = 42L;

        String simulatedPath = "/storage/uploads-fiis/unique-uuid_tesis_sistema_fiis.pdf";
        when(fileStoragePort.store(any(InputStream.class), eq(fileName))).thenReturn(simulatedPath);

        Document simulatedSavedDoc = Document.builder()
                .id(1L)
                .originalName(fileName)
                .storagePath(simulatedPath)
                .extension("PDF")
                .sizeBytes(sizeBytes)
                .uploadedById(userId)
                .active(true)
                .build();
        when(documentRepositoryPort.save(any(Document.class))).thenReturn(simulatedSavedDoc);

        // Act (Ejecución)
        DocumentResponseDto result = uploadDocumentUseCase.execute(fakeStream, fileName, sizeBytes, userId);

        // Assert (Verificación de requisitos)
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("PDF", result.extension());
        assertEquals(fileName, result.originalName());
        verify(fileStoragePort, times(1)).store(fakeStream, fileName);
        verify(documentRepositoryPort, times(1)).save(any(Document.class));
    }




    @Test
    @DisplayName("RNF-07: Intento de carga fallida debido a extensión inválida o maliciosa")
    void uploadDocument_InvalidExtension_ThrowsException() {
        // Arrange
        InputStream fakeStream = new ByteArrayInputStream("bytes".getBytes());
        String fileName = "script_malicioso.exe";
        Long sizeBytes = 500L;
        Long userId = 42L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> uploadDocumentUseCase.execute(fakeStream, fileName, sizeBytes, userId));

        assertTrue(exception.getMessage().contains("Extensión de archivo no permitida"));
        verify(documentRepositoryPort, never()).save(any(Document.class));
    }

    @Test
    @DisplayName("RF-67 / RNF-08: Descarga exitosa si el solicitante es el propietario del documento")
    void downloadDocument_AsOwner_Success() {
        // Arrange
        Long docId = 10L;
        Long userId = 77L;
        String userRole = "ESTUDIANTE";

        Document stubDoc = Document.builder()
                .id(docId)
                .originalName("plan_de_tesis.docx")
                .storagePath("/path/plan.docx")
                .extension("DOCX")
                .uploadedById(userId) // Mismo ID de usuario
                .active(true)
                .build();

        when(documentRepositoryPort.findById(docId)).thenReturn(Optional.of(stubDoc));
        when(fileStoragePort.load(stubDoc.getStoragePath())).thenReturn(new ByteArrayInputStream("contenido".getBytes()));

        // Act
        DocumentDownloadResult result = downloadDocumentUseCase.execute(docId, userId, userRole);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getInputStream());
        assertEquals("plan_de_tesis.docx", result.getOriginalName());
        verify(fileStoragePort, times(1)).load(stubDoc.getStoragePath());
    }

    @Test
    @DisplayName("RN-04 / RNF-03: Descarga denegada si un usuario ajeno sin rol jerárquico intenta acceder")
    void downloadDocument_UnauthorizedUser_ThrowsException() {
        // Arrange
        Long docId = 10L;
        Long ownerId = 77L;
        Long foreignUserId = 99L; // Usuario ajeno
        String foreignUserRole = "DOCENTE_INVESTIGADOR"; // No es autoridad central (Director/Decano)

        Document stubDoc = Document.builder()
                .id(docId)
                .originalName("plan_de_tesis.docx")
                .storagePath("/path/plan.docx")
                .extension("DOCX")
                .uploadedById(ownerId)
                .active(true)
                .build();

        when(documentRepositoryPort.findById(docId)).thenReturn(Optional.of(stubDoc));

        // Act & Assert
        assertThrows(DocumentAccessDeniedException.class,
                () -> downloadDocumentUseCase.execute(docId, foreignUserId, foreignUserRole));

        verify(fileStoragePort, never()).load(anyString());
    }

    @Test
    @DisplayName("RF-67: Autoridades de la facultad (Director de Investigación) pueden descargar cualquier documento")
    void downloadDocument_AsAuthority_Success() {
        // Arrange
        Long docId = 15L;
        Long ownerId = 11L;
        Long directorUserId = 2L; // ID del Director
        String directorRole = "DIRECTOR_INVESTIGACION"; // Rol con privilegios institucionales

        Document stubDoc = Document.builder()
                .id(docId)
                .originalName("proyecto_confidencial.pdf")
                .storagePath("/path/proyecto.pdf")
                .extension("PDF")
                .uploadedById(ownerId)
                .active(true)
                .build();

        when(documentRepositoryPort.findById(docId)).thenReturn(Optional.of(stubDoc));
        when(fileStoragePort.load(stubDoc.getStoragePath())).thenReturn(new ByteArrayInputStream("datos".getBytes()));

        // Act
        DocumentDownloadResult result = downloadDocumentUseCase.execute(docId, directorUserId, directorRole);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getInputStream());
        assertEquals("proyecto_confidencial.pdf", result.getOriginalName());
        verify(fileStoragePort, times(1)).load(stubDoc.getStoragePath());
    }
}