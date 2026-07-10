package com.sgi.fiis.documentacion.application.usecase;

import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;
import com.sgi.fiis.documentacion.domain.model.Document;
import com.sgi.fiis.documentacion.domain.port.DocumentRepositoryPort;
import com.sgi.fiis.documentacion.domain.port.FileStoragePort;
import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.domain.port.out.ResolutionRepositoryPort;
import com.sgi.fiis.tramites.domain.model.Procedure;
import com.sgi.fiis.tramites.domain.port.ProcedureRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Pruebas de Caso de Uso - DownloadDocument")
class DownloadDocumentUseCaseTest {

    private DocumentRepositoryPort documentRepositoryPort;
    private FileStoragePort fileStoragePort;
    private ResolutionRepositoryPort resolutionRepositoryPort;
    private ProcedureRepositoryPort procedureRepositoryPort;
    private DownloadDocumentUseCase downloadDocumentUseCase;

    @BeforeEach
    void setUp() {
        documentRepositoryPort = mock(DocumentRepositoryPort.class);
        fileStoragePort = mock(FileStoragePort.class);
        resolutionRepositoryPort = mock(ResolutionRepositoryPort.class);
        procedureRepositoryPort = mock(ProcedureRepositoryPort.class);

        downloadDocumentUseCase =
                new DownloadDocumentUseCase(
                        documentRepositoryPort,
                        fileStoragePort,
                        resolutionRepositoryPort,
                        procedureRepositoryPort
                );
    }

    @Test
    @DisplayName("Debe permitir descarga si el usuario es propietario")
    void execute_Success_WhenUserIsOwner() {

        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("tesis.pdf")
                .extension("PDF")
                .sizeBytes(2048L)
                .storagePath("/ruta/archivo.pdf")
                .uploadedById(42L)
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

        DocumentDownloadResult result =
                downloadDocumentUseCase.execute(
                        1L,
                        42L,
                        "ESTUDIANTE"
                );

        assertNotNull(result);
        assertEquals("tesis.pdf", result.getOriginalName());
        assertNotNull(result.getInputStream());

        verify(fileStoragePort)
                .load("/ruta/archivo.pdf");
    }

    @Test
    @DisplayName("Debe permitir descarga si el usuario es autoridad")
    void execute_Success_WhenUserIsAuthority() {

        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("autoridad.pdf")
                .extension("PDF")
                .sizeBytes(2048L)
                .storagePath("/ruta/autoridad.pdf")
                .uploadedById(10L)
                .active(true)
                .build();

        InputStream fakeStream =
                new ByteArrayInputStream(
                        "datos-admin".getBytes()
                );

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.of(mockDoc));

        when(fileStoragePort.load("/ruta/autoridad.pdf"))
                .thenReturn(fakeStream);

        DocumentDownloadResult result =
                downloadDocumentUseCase.execute(
                        1L,
                        99L,
                        "ADMIN"
                );

        assertNotNull(result);
        assertEquals("autoridad.pdf", result.getOriginalName());
        assertNotNull(result.getInputStream());

        verify(fileStoragePort)
                .load("/ruta/autoridad.pdf");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el documento no existe")
    void execute_ThrowsException_WhenDocumentNotFound() {

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.empty());

        DocumentNotFoundException exception =
                assertThrows(
                        DocumentNotFoundException.class,
                        () -> downloadDocumentUseCase.execute(
                                1L,
                                42L,
                                "ESTUDIANTE"
                        )
                );

        assertEquals(
                "El documento no existe.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción si el documento está inactivo")
    void execute_ThrowsException_WhenDocumentIsInactive() {

        Document inactiveDoc = Document.builder()
                .id(1L)
                .originalName("tesis.pdf")
                .extension("PDF")
                .sizeBytes(2048L)
                .storagePath("/ruta/inactivo.pdf")
                .uploadedById(42L)
                .active(false)
                .build();

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.of(inactiveDoc));

        DocumentAccessDeniedException exception =
                assertThrows(
                        DocumentAccessDeniedException.class,
                        () -> downloadDocumentUseCase.execute(
                                1L,
                                42L,
                                "ESTUDIANTE"
                        )
                );

        assertEquals(
                "El documento solicitado no está disponible.",
                exception.getMessage()
        );
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no tiene permisos")
    void execute_ThrowsException_WhenAccessDenied() {

        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("tesis.pdf")
                .extension("PDF")
                .sizeBytes(2048L)
                .storagePath("/ruta/prohibido.pdf")
                .uploadedById(10L)
                .active(true)
                .build();

        when(documentRepositoryPort.findById(1L))
                .thenReturn(Optional.of(mockDoc));

        DocumentAccessDeniedException exception =
                assertThrows(
                        DocumentAccessDeniedException.class,
                        () -> downloadDocumentUseCase.execute(
                                1L,
                                99L,
                                "ESTUDIANTE"
                        )
                );

        assertTrue(
                exception.getMessage()
                        .contains("Acceso denegado")
        );
    }

    @Test
    @DisplayName("Debe permitir descarga si el usuario es el solicitante del trámite asociado")
    void execute_Success_WhenUserIsProcedureApplicant() {
        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("resolucion.pdf")
                .extension("PDF")
                .sizeBytes(2048L)
                .storagePath("/ruta/resolucion.pdf")
                .uploadedById(10L)
                .active(true)
                .build();

        Resolution resolution = new Resolution(
                1L, "RES-001", null, null, 5L, 1L, null);

        Procedure procedure = Procedure.builder()
                .id(5L)
                .idSolicitante(77L)
                .build();

        InputStream fakeStream = new ByteArrayInputStream("contenido".getBytes());

        when(documentRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoc));
        when(resolutionRepositoryPort.findByDocumentAdjuntoId(1L)).thenReturn(Optional.of(resolution));
        when(procedureRepositoryPort.findById(5L)).thenReturn(Optional.of(procedure));
        when(fileStoragePort.load("/ruta/resolucion.pdf")).thenReturn(fakeStream);

        DocumentDownloadResult result = downloadDocumentUseCase.execute(1L, 77L, "ESTUDIANTE");

        assertNotNull(result);
        assertEquals("resolucion.pdf", result.getOriginalName());
        verify(fileStoragePort).load("/ruta/resolucion.pdf");
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no es propietario ni autoridad ni solicitante")
    void execute_ThrowsException_WhenUserIsNotApplicant() {
        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("resolucion.pdf")
                .extension("PDF")
                .sizeBytes(2048L)
                .storagePath("/ruta/resolucion.pdf")
                .uploadedById(10L)
                .active(true)
                .build();

        when(documentRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoc));
        when(resolutionRepositoryPort.findByDocumentAdjuntoId(1L)).thenReturn(Optional.empty());

        DocumentAccessDeniedException exception = assertThrows(
                DocumentAccessDeniedException.class,
                () -> downloadDocumentUseCase.execute(1L, 99L, "ESTUDIANTE")
        );

        assertTrue(exception.getMessage().contains("Acceso denegado"));
    }

    @Test
    @DisplayName("Debe permitir descarga con rol DIRECTOR_INVESTIGACION")
    void execute_Success_WhenUserIsDirectorInvestigacion() {
        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("doc_director.pdf")
                .extension("PDF")
                .sizeBytes(1024L)
                .storagePath("/ruta/director.pdf")
                .uploadedById(10L)
                .active(true)
                .build();

        InputStream fakeStream = new ByteArrayInputStream("datos".getBytes());

        when(documentRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoc));
        when(fileStoragePort.load("/ruta/director.pdf")).thenReturn(fakeStream);

        DocumentDownloadResult result = downloadDocumentUseCase.execute(1L, 50L, "DIRECTOR_INVESTIGACION");

        assertNotNull(result);
        assertEquals("doc_director.pdf", result.getOriginalName());
    }

    @Test
    @DisplayName("Debe permitir descarga con rol COORDINADOR_GRUPO")
    void execute_Success_WhenUserIsCoordinadorGrupo() {
        Document mockDoc = Document.builder()
                .id(1L)
                .originalName("doc_coord.pdf")
                .extension("PDF")
                .sizeBytes(1024L)
                .storagePath("/ruta/coord.pdf")
                .uploadedById(10L)
                .active(true)
                .build();

        InputStream fakeStream = new ByteArrayInputStream("datos".getBytes());

        when(documentRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoc));
        when(fileStoragePort.load("/ruta/coord.pdf")).thenReturn(fakeStream);

        DocumentDownloadResult result = downloadDocumentUseCase.execute(1L, 50L, "COORDINADOR_GRUPO");

        assertNotNull(result);
        assertEquals("doc_coord.pdf", result.getOriginalName());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando fileStoragePort.load falla")
    void execute_ThrowsException_WhenFileStorageFails() {
        Document mockDoc = Document.builder()
                .id(1L).originalName("test.pdf").extension("PDF")
                .sizeBytes(1024L).storagePath("/ruta/test.pdf")
                .uploadedById(42L).active(true).build();

        when(documentRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoc));
        when(fileStoragePort.load("/ruta/test.pdf"))
                .thenThrow(new RuntimeException("File not found"));

        DocumentNotFoundException ex = assertThrows(
                DocumentNotFoundException.class,
                () -> downloadDocumentUseCase.execute(1L, 42L, "ESTUDIANTE"));

        assertEquals("No se pudo acceder al archivo del documento.", ex.getMessage());
    }

    @Test
    @DisplayName("Debe permitir descarga cuando uploadedById es null")
    void execute_Success_WhenUploadedByIdIsNull() {
        Document mockDoc = Document.builder()
                .id(1L).originalName("anon.pdf").extension("PDF")
                .sizeBytes(1024L).storagePath("/ruta/anon.pdf")
                .uploadedById(null).active(true).build();

        InputStream fakeStream = new ByteArrayInputStream("datos".getBytes());

        when(documentRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoc));
        when(fileStoragePort.load("/ruta/anon.pdf")).thenReturn(fakeStream);

        DocumentDownloadResult result = downloadDocumentUseCase.execute(1L, 42L, "ADMIN");

        assertNotNull(result);
        assertEquals("anon.pdf", result.getOriginalName());
    }

    @Test
    @DisplayName("Debe permitir descarga cuando currentUserRol es null y usuario es propietario")
    void execute_Success_WhenRoleIsNullAndUserIsOwner() {
        Document mockDoc = Document.builder()
                .id(1L).originalName("owner.pdf").extension("PDF")
                .sizeBytes(1024L).storagePath("/ruta/owner.pdf")
                .uploadedById(42L).active(true).build();

        InputStream fakeStream = new ByteArrayInputStream("datos".getBytes());

        when(documentRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoc));
        when(fileStoragePort.load("/ruta/owner.pdf")).thenReturn(fakeStream);

        DocumentDownloadResult result = downloadDocumentUseCase.execute(1L, 42L, null);

        assertNotNull(result);
        assertEquals("owner.pdf", result.getOriginalName());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando procedure tiene idSolicitante null")
    void execute_ThrowsException_WhenProcedureApplicantIsNull() {
        Document mockDoc = Document.builder()
                .id(1L).originalName("res.pdf").extension("PDF")
                .sizeBytes(1024L).storagePath("/ruta/res.pdf")
                .uploadedById(10L).active(true).build();

        Resolution resolution = new Resolution(1L, "RES-001", null, null, 5L, 1L, null);
        Procedure procedure = Procedure.builder().id(5L).idSolicitante(null).build();

        when(documentRepositoryPort.findById(1L)).thenReturn(Optional.of(mockDoc));
        when(resolutionRepositoryPort.findByDocumentAdjuntoId(1L)).thenReturn(Optional.of(resolution));
        when(procedureRepositoryPort.findById(5L)).thenReturn(Optional.of(procedure));

        DocumentAccessDeniedException ex = assertThrows(
                DocumentAccessDeniedException.class,
                () -> downloadDocumentUseCase.execute(1L, 99L, "ESTUDIANTE"));

        assertTrue(ex.getMessage().contains("Acceso denegado"));
    }
}