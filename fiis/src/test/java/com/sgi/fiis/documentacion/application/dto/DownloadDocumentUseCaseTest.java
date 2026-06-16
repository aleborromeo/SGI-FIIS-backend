package com.sgi.fiis.documentacion.application.dto;

import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas de Caso de Uso - DownloadDocumentUseCase")
class DownloadDocumentUseCaseTest {

    private DocumentRepositoryPort documentRepositoryPort;
    private FileStoragePort fileStoragePort;
    private DownloadDocumentUseCase downloadDocumentUseCase;

    @BeforeEach
    void setUp() {
        documentRepositoryPort = mock(DocumentRepositoryPort.class);
        fileStoragePort = mock(FileStoragePort.class);

        downloadDocumentUseCase = new DownloadDocumentUseCase(
                documentRepositoryPort,
                fileStoragePort
        );
    }

    @Test
    @DisplayName("Debe permitir descarga cuando el usuario es el propietario")
    void execute_Success_WhenUserIsOwner() {

        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("tesis.pdf")
                .extension("PDF")
                .sizeBytes(2048L)
                .storagePath("/ruta/archivo.pdf")
                .uploadedById(42)
                .active(true)
                .build();

        InputStream fakeStream =
                new ByteArrayInputStream(
                        "contenido".getBytes()
                );

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.of(mockDoc));

        when(fileStoragePort.load("/ruta/archivo.pdf"))
                .thenReturn(fakeStream);

        InputStream result =
                downloadDocumentUseCase.execute(
                        1L,
                        42,
                        "ESTUDIANTE"
                );

        assertNotNull(result);

        verify(fileStoragePort)
                .load("/ruta/archivo.pdf");
    }

    @Test
    @DisplayName("Debe permitir descarga cuando el usuario tiene rol ADMIN")
    void execute_Success_WhenUserIsAuthority() {

        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("documento.pdf")
                .extension("PDF")
                .sizeBytes(1024L)
                .storagePath("/ruta/admin.pdf")
                .uploadedById(10)
                .active(true)
                .build();

        InputStream fakeStream =
                new ByteArrayInputStream(
                        "datos-admin".getBytes()
                );

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.of(mockDoc));

        when(fileStoragePort.load("/ruta/admin.pdf"))
                .thenReturn(fakeStream);

        InputStream result =
                downloadDocumentUseCase.execute(
                        1L,
                        99,
                        "ADMIN"
                );

        assertNotNull(result);

        verify(fileStoragePort)
                .load("/ruta/admin.pdf");
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException cuando el documento no existe")
    void execute_ThrowsException_WhenDocumentNotFound() {

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> downloadDocumentUseCase.execute(
                                1L,
                                42,
                                "ESTUDIANTE"
                        )
                );

        assertEquals(
                "El documento no existe.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException cuando el documento está inactivo")
    void execute_ThrowsException_WhenDocumentIsInactive() {

        Document inactiveDoc = Document.builder()
                .id(1L)
                .originalName("inactivo.pdf")
                .extension("PDF")
                .sizeBytes(1024L)
                .storagePath("/ruta/inactivo.pdf")
                .uploadedById(42)
                .active(false)
                .build();

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.of(inactiveDoc));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> downloadDocumentUseCase.execute(
                                1L,
                                42,
                                "ESTUDIANTE"
                        )
                );

        assertEquals(
                "El documento solicitado no está disponible.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Debe lanzar RuntimeException cuando el usuario no tiene permisos")
    void execute_ThrowsException_WhenAccessDenied() {

        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("privado.pdf")
                .extension("PDF")
                .sizeBytes(4096L)
                .storagePath("/ruta/privado.pdf")
                .uploadedById(10)
                .active(true)
                .build();

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.of(mockDoc));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> downloadDocumentUseCase.execute(
                                1L,
                                99,
                                "ESTUDIANTE"
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("Acceso denegado")
        );
    }
}