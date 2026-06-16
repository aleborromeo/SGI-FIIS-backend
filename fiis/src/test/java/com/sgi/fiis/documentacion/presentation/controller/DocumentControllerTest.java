package com.sgi.fiis.documentacion.presentation.controller;

import com.sgi.fiis.documentacion.application.dto.DocumentResponseDto;
import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;
import com.sgi.fiis.documentacion.application.usecase.DeactivateDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.DownloadDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.UploadDocumentUseCase;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

    @Test
    @DisplayName("HTTP POST: /api/documents/upload debe retornar 201 Created ante archivo válido")
    void uploadDocument_HttpSuccess() throws Exception {

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "manual_investigacion.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "contenido-binario-de-prueba".getBytes()
        );

        DocumentResponseDto simulatedDto =
                new DocumentResponseDto(
                        1L,
                        "manual_investigacion.pdf",
                        "PDF",
                        100L,
                        42,
                        LocalDateTime.now()
                );

        when(uploadDocumentUseCase.execute(
                any(),
                eq("manual_investigacion.pdf"),
                anyLong(),
                eq(42)
        )).thenReturn(simulatedDto);

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(mockFile)
                                .param("userId", "42")
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
                                .param("userId", "42")
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
                "datos-de-prueba".getBytes()
        );

        // Cambiamos a IllegalArgumentException para que sea un error no verificado compatible con el Servlet de MockMvc
        when(uploadDocumentUseCase.execute(any(), any(), anyLong(), anyInt()))
                .thenThrow(new IllegalArgumentException("Error de lectura física en el sistema de almacenamiento"));

        mockMvc.perform(
                        multipart("/api/documents/upload")
                                .file(mockFile)
                                .param("userId", "42")
                )
                .andExpect(status().isBadRequest())
                .andExpect(header().string("X-Error-Cause", "Error de lectura física en el sistema de almacenamiento"));
    }

    @Test
    @DisplayName("HTTP GET download retorna 200")
    void downloadDocument_HttpSuccess() throws Exception {

        Long documentId = 1L;
        Integer userId = 42;
        String role = "ESTUDIANTE";

        ByteArrayInputStream fakeInputStream =
                new ByteArrayInputStream(
                        "archivo-descargado".getBytes()
                );

        when(downloadDocumentUseCase.execute(
                eq(documentId),
                eq(userId),
                eq(role)
        )).thenReturn(fakeInputStream);

        mockMvc.perform(
                        get("/api/documents/download/{id}", documentId)
                                .param("userId", userId.toString())
                                .param("userRole", role)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE));
    }

    @Test
    @DisplayName("HTTP GET download retorna 403")
    void downloadDocument_HttpForbidden() throws Exception {

        Long documentId = 1L;
        Integer intruderId = 99;
        String role = "ESTUDIANTE";

        when(downloadDocumentUseCase.execute(
                eq(documentId),
                eq(intruderId),
                eq(role)
        )).thenThrow(
                new DocumentAccessDeniedException(
                        "Acceso denegado"
                )
        );

        mockMvc.perform(
                        get("/api/documents/download/{id}", documentId)
                                .param("userId", intruderId.toString())
                                .param("userRole", role)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("HTTP GET download retorna 404 si el documento no existe")
    void downloadDocument_HttpNotFound() throws Exception {

        Long nonExistentId = 404L;

        // Se usa una excepción del paquete para garantizar la inyección correcta
        when(downloadDocumentUseCase.execute(any(), any(), any()))
                .thenThrow(new DocumentNotFoundException("El documento solicitado no existe."));

        mockMvc.perform(
                        get("/api/documents/download/{id}", nonExistentId)
                                .param("userId", "42")
                                .param("userRole", "ESTUDIANTE")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("HTTP DELETE deactivate retorna 204")
    void deactivateDocument_HttpSuccess() throws Exception {

        Long documentId = 1L;

        doNothing().when(deactivateDocumentUseCase)
                .execute(eq(documentId),
                        eq(42),
                        eq("ADMIN"));

        mockMvc.perform(
                        delete("/api/documents/deactivate/{id}", documentId)
                                .param("userId", "42")
                                .param("userRole", "ADMIN")
                )
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("HTTP DELETE deactivate retorna 403 si el usuario no tiene permisos")
    void deactivateDocument_HttpForbidden() throws Exception {

        Long documentId = 1L;

        doThrow(new DocumentAccessDeniedException("No tiene permisos para deshabilitar este documento."))
                .when(deactivateDocumentUseCase)
                .execute(eq(documentId), any(Integer.class), any(String.class));

        mockMvc.perform(
                        delete("/api/documents/deactivate/{id}", documentId)
                                .param("userId", "42")
                                .param("userRole", "ESTUDIANTE")
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
                        any(Integer.class),
                        any(String.class));

        mockMvc.perform(
                        delete("/api/documents/deactivate/{id}", nonExistentId)
                                .param("userId", "42")
                                .param("userRole", "ADMIN")
                )
                .andExpect(status().isNotFound());
    }
}