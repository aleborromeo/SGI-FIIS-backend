package com.sgi.fiis.documentacion.presentation.controller;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.documentacion.application.dto.DocumentResponseDto;
import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;
import com.sgi.fiis.documentacion.application.usecase.DeactivateDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.DownloadDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.ListDocumentsUseCase;
import com.sgi.fiis.documentacion.application.usecase.UploadDocumentUseCase;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import com.sgi.fiis.documentacion.application.usecase.DocumentDownloadResult;
import org.springframework.http.HttpHeaders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(DocumentController.class)
@DisplayName("Pruebas Expandidas de Cobertura - DocumentController")
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UploadDocumentUseCase uploadDocumentUseCase;

    @MockitoBean
    private DownloadDocumentUseCase downloadDocumentUseCase;

    @MockitoBean
    private DeactivateDocumentUseCase deactivateDocumentUseCase;

    @MockitoBean
    private ListDocumentsUseCase listDocumentsUseCase;

    @MockitoBean
    private com.sgi.fiis.auth.domain.port.TokenProviderPort tokenProviderPort;

    @MockitoBean
    private com.sgi.fiis.auth.infrastructure.security.CustomUserDetailsService customUserDetailsService;

    /**
     * Crea un Authentication simulado con CustomUserDetails como principal mockeado.
     */
    private UsernamePasswordAuthenticationToken createAuth(Long userId, String role) {
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getId()).thenReturn(userId);
        when(userDetails.getUsername()).thenReturn("testuser@unas.edu.pe");
        when(userDetails.getAuthorities())
                .thenReturn((java.util.Collection) List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP POST: /api/documents/upload debe retornar 201 Created ante archivo válido")
    void uploadDocument_HttpSuccess() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "manual_investigacion.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "%PDF-1.4 contenido-binario-de-prueba".getBytes()
        );

        DocumentResponseDto simulatedDto =
                new DocumentResponseDto(
                        1L,
                        "manual_investigacion.pdf",
                        "PDF",
                        100L,
                        42L,
                        LocalDateTime.of(2026, java.time.Month.JUNE, 17, 10, 0)
                );

        when(uploadDocumentUseCase.execute(
                any(),
                eq("manual_investigacion.pdf"),
                anyLong(),
                eq(42L)
        )).thenReturn(simulatedDto);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(mockFile)
                                .principal(createAuth(42L, "ESTUDIANTE"))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.originalName").value("manual_investigacion.pdf"))
                .andExpect(jsonPath("$.extension").value("PDF"));
    }



    @Test
    @DisplayName("HTTP POST: /api/documents/upload debe retornar 400 Bad Request si el archivo está vacío")
    void uploadDocument_HttpBadRequest_EmptyFile() throws Exception {
        
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "vacio.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                new byte[0]
        );

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(emptyFile)
                                .principal(createAuth(42L, "ESTUDIANTE"))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("HTTP POST: /api/documents/upload debe retornar 400 Bad Request ante un error controlado de parámetros")
    void uploadDocument_HttpBadRequest_RuntimeException() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "error.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "%PDF-1.4 datos-de-prueba".getBytes()
        );

        // Cambiamos a IllegalArgumentException para que sea un error no verificado compatible con el Servlet de MockMvc
        when(uploadDocumentUseCase.execute(any(), any(), anyLong(), anyLong()))
                .thenThrow(new IllegalArgumentException("Error de lectura física en el sistema de almacenamiento"));

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(mockFile)
                                .principal(createAuth(42L, "ESTUDIANTE"))
                )
                .andExpect(status().isBadRequest())
                .andExpect(header().string("X-Error-Cause", "Error de lectura física en el sistema de almacenamiento"));
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP GET download retorna 200")
    void downloadDocument_HttpSuccess() throws Exception {

        Long documentId = 1L;
        Long userId = 42L;
        String role = "ESTUDIANTE";

        ByteArrayInputStream fakeInputStream =
                new ByteArrayInputStream(
                        "archivo-descargado".getBytes()
                );

        when(downloadDocumentUseCase.execute(
                documentId,
                userId,
                role
        )).thenReturn(new DocumentDownloadResult(fakeInputStream, "tesis_descarga.pdf"));

        mockMvc.perform(
                        get("/api/documents/download/{id}", documentId)
                                .principal(createAuth(userId, role))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tesis_descarga.pdf\""));
    }

    @Test
    @DisplayName("HTTP GET download retorna 403")
    void downloadDocument_HttpForbidden() throws Exception {

        Long documentId = 1L;
        Long intruderId = 99L;
        String role = "ESTUDIANTE";

        when(downloadDocumentUseCase.execute(
                documentId,
                intruderId,
                role
        )).thenThrow(
                new DocumentAccessDeniedException(
                        "Acceso denegado"
                )
        );

        mockMvc.perform(
                        get("/api/documents/download/{id}", documentId)
                                .principal(createAuth(intruderId, role))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("HTTP GET download retorna 404 si el documento no existe")
    void downloadDocument_HttpNotFound() throws Exception {

        Long nonExistentId = 404L;

        // Se usa una excepción del paquete para garantizar la inyección correcta
        when(downloadDocumentUseCase.execute(anyLong(), anyLong(), anyString()))
                .thenThrow(new DocumentNotFoundException("El documento solicitado no existe."));

        mockMvc.perform(
                        get("/api/documents/download/{id}", nonExistentId)
                                .principal(createAuth(42L, "ESTUDIANTE"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP DELETE deactivate retorna 204")
    void deactivateDocument_HttpSuccess() throws Exception {

        Long documentId = 1L;

        doNothing().when(deactivateDocumentUseCase)
                .execute(documentId,
                        42L,
                        "ADMIN");

        mockMvc.perform(
                        delete("/api/documents/deactivate/{id}", documentId)
                                .principal(createAuth(42L, "ADMIN"))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("HTTP DELETE deactivate retorna 403 si el usuario no tiene permisos")
    void deactivateDocument_HttpForbidden() throws Exception {

        Long documentId = 1L;

        doThrow(new DocumentAccessDeniedException("No tiene permisos para deshabilitar este documento."))
                .when(deactivateDocumentUseCase)
                .execute(eq(documentId), anyLong(), anyString());

        mockMvc.perform(
                        delete("/api/documents/deactivate/{id}", documentId)
                                .principal(createAuth(42L, "ESTUDIANTE"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("HTTP DELETE deactivate retorna 404")
    void deactivateDocument_HttpNotFound() throws Exception {

        Long nonExistentId = 404L;

        doThrow(new DocumentNotFoundException(
                "El documento no existe."
        ))
                .when(deactivateDocumentUseCase)
                .execute(eq(nonExistentId),
                        anyLong(),
                        anyString());

        mockMvc.perform(
                        delete("/api/documents/deactivate/{id}", nonExistentId)
                                .principal(createAuth(42L, "ADMIN"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP GET /api/documents retorna 200 con lista de documentos")
    void listDocuments_HttpSuccess() throws Exception {
        DocumentResponseDto doc1 = new DocumentResponseDto(
                1L, "informe.pdf", "PDF", 1024L, 10L,
                LocalDateTime.of(2026, java.time.Month.JUNE, 17, 10, 0));
        DocumentResponseDto doc2 = new DocumentResponseDto(
                2L, "tesis.docx", "DOCX", 2048L, 20L,
                LocalDateTime.of(2026, java.time.Month.JUNE, 18, 10, 0));

        when(listDocumentsUseCase.execute()).thenReturn(List.of(doc1, doc2));

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].originalName").value("informe.pdf"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].originalName").value("tesis.docx"));
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP GET /api/documents retorna 200 con lista vacía")
    void listDocuments_HttpEmpty() throws Exception {
        when(listDocumentsUseCase.execute()).thenReturn(List.of());

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("HTTP POST: /api/documents/upload debe retornar 400 ante tipo MIME no permitido")
    void uploadDocument_HttpBadRequest_InvalidMimeType() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "imagen.png",
                "image/png",
                "contenido-de-imagen".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(mockFile)
                                .principal(createAuth(42L, "ESTUDIANTE"))
                )
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error-Cause"));
    }

    @Test
    @DisplayName("HTTP POST: /api/documents/upload debe retornar 400 ante content type null")
    void uploadDocument_HttpBadRequest_NullContentType() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "sin_tipo.bin",
                null,
                "contenido".getBytes()
        );

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(mockFile)
                                .principal(createAuth(42L, "ESTUDIANTE"))
                )
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error-Cause"));
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP POST: upload acepta archivo DOC con magic bytes correctos")
    void uploadDocument_HttpSuccess_DocFile() throws Exception {
        byte[] docContent = new byte[]{(byte)0xD0, (byte)0xCF, 0x11, 0x00, 0x00};
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "doc.doc", "application/msword", docContent);
        DocumentResponseDto dto = new DocumentResponseDto(
                1L, "doc.doc", "DOC", 100L, 42L,
                LocalDateTime.of(2026, java.time.Month.JUNE, 17, 10, 0));
        when(uploadDocumentUseCase.execute(any(), eq("doc.doc"), anyLong(), eq(42L))).thenReturn(dto);
        mockMvc.perform(multipart("/api/documents/upload").file(mockFile)
                        .principal(createAuth(42L, "ESTUDIANTE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalName").value("doc.doc"));
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP POST: upload acepta archivo DOCX con magic bytes correctos")
    void uploadDocument_HttpSuccess_DocxFile() throws Exception {
        byte[] docxContent = new byte[]{(byte)'P', (byte)'K', 0x03, 0x04, 0x00};
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "doc.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                docxContent);
        DocumentResponseDto dto = new DocumentResponseDto(
                1L, "doc.docx", "DOCX", 100L, 42L,
                LocalDateTime.of(2026, java.time.Month.JUNE, 17, 10, 0));
        when(uploadDocumentUseCase.execute(any(), eq("doc.docx"), anyLong(), eq(42L))).thenReturn(dto);
        mockMvc.perform(multipart("/api/documents/upload").file(mockFile)
                        .principal(createAuth(42L, "ESTUDIANTE")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalName").value("doc.docx"));
    }

    @Test
    @DisplayName("HTTP POST: upload rechaza archivo con magic bytes inválidos")
    void uploadDocument_HttpBadRequest_InvalidMagicBytes() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "fake.pdf", MediaType.APPLICATION_PDF_VALUE,
                "not-a-real-pdf".getBytes());
        mockMvc.perform(multipart("/api/documents/upload").file(mockFile)
                        .principal(createAuth(42L, "ESTUDIANTE")))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("X-Error-Cause",
                        "El contenido del archivo no coincide con el tipo declarado."));
    }

    @Test
    @DisplayName("HTTP POST: upload rechaza archivo con contenido muy corto")
    void uploadDocument_HttpBadRequest_ShortContent() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "tiny.pdf", MediaType.APPLICATION_PDF_VALUE,
                new byte[]{0x01, 0x02});
        mockMvc.perform(multipart("/api/documents/upload").file(mockFile)
                        .principal(createAuth(42L, "ESTUDIANTE")))
                .andExpect(status().isBadRequest())
                .andExpect(header().exists("X-Error-Cause"));
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP GET download sanitiza filename con CRLF")
    void downloadDocument_HttpSuccess_SanitizesCrlf() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("data".getBytes());
        when(downloadDocumentUseCase.execute(1L, 42L, "ESTUDIANTE"))
                .thenReturn(new DocumentDownloadResult(stream, "file\r\nInjected.pdf"));
        mockMvc.perform(get("/api/documents/download/{id}", 1L)
                        .principal(createAuth(42L, "ESTUDIANTE")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"file__Injected.pdf\""));
    }

    @Test
    @WithMockUser(username = "docente@unas.edu.pe", roles = {"DOCENTE_INVESTIGADOR"})
    @DisplayName("HTTP GET download sanitiza filename con caracteres especiales")
    void downloadDocument_HttpSuccess_SanitizesSpecialChars() throws Exception {
        ByteArrayInputStream stream = new ByteArrayInputStream("data".getBytes());
        when(downloadDocumentUseCase.execute(1L, 42L, "ESTUDIANTE"))
                .thenReturn(new DocumentDownloadResult(stream, "archivo (copia).pdf"));
        mockMvc.perform(get("/api/documents/download/{id}", 1L)
                        .principal(createAuth(42L, "ESTUDIANTE")))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"archivo__copia_.pdf\""));
    }

    @Test
    @DisplayName("HTTP POST: upload retorna 401 cuando no hay usuario autenticado")
    void uploadDocument_HttpUnauthorized_WhenNoUserId() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.pdf", MediaType.APPLICATION_PDF_VALUE,
                "%PDF-1.4 content".getBytes());
        mockMvc.perform(multipart("/api/documents/upload").file(mockFile))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("HTTP GET download retorna 401 cuando no hay usuario autenticado")
    void downloadDocument_HttpUnauthorized_WhenNoUserId() throws Exception {
        mockMvc.perform(get("/api/documents/download/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("HTTP DELETE deactivate retorna 401 cuando no hay usuario autenticado")
    void deactivateDocument_HttpUnauthorized_WhenNoUserId() throws Exception {
        mockMvc.perform(delete("/api/documents/deactivate/{id}", 1L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("HTTP POST: upload con content-type application/zip (no permitido)")
    void uploadDocument_HttpBadRequest_ZipMimeType() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "file.zip", "application/zip", "content".getBytes());
        mockMvc.perform(multipart("/api/documents/upload").file(mockFile)
                        .principal(createAuth(42L, "ESTUDIANTE")))
                .andExpect(status().isBadRequest());
    }
}