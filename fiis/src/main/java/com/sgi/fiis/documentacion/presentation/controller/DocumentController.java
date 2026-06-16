package com.sgi.fiis.documentacion.presentation.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sgi.fiis.documentacion.application.dto.DocumentResponseDto;
import com.sgi.fiis.documentacion.application.usecase.DeactivateDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.DownloadDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.UploadDocumentUseCase;
import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final UploadDocumentUseCase uploadDocumentUseCase;
    private final DownloadDocumentUseCase downloadDocumentUseCase;
    private final DeactivateDocumentUseCase deactivateDocumentUseCase; // Inyección faltante corregida

    // Constructor unificado con los 3 casos de uso requeridos
    public DocumentController(UploadDocumentUseCase uploadDocumentUseCase, 
                              DownloadDocumentUseCase downloadDocumentUseCase,
                              DeactivateDocumentUseCase deactivateDocumentUseCase) {
        this.uploadDocumentUseCase = uploadDocumentUseCase;
        this.downloadDocumentUseCase = downloadDocumentUseCase;
        this.deactivateDocumentUseCase = deactivateDocumentUseCase;
    }

    // Endpoint para Carga de Documentos (RF-65)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Integer userId) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            DocumentResponseDto response = uploadDocumentUseCase.execute(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getSize(),
                userId
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().header("X-Error-Cause", e.getMessage()).build();
        }
    }

    // Endpoint para Descarga Segura de Archivos (RF-67 / RNF-08)
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable("id") Long documentId,
            @RequestParam("userId") Integer currentUserId,
            @RequestParam("userRole") String currentUserRole) {

        try {
            InputStream fileStream = downloadDocumentUseCase.execute(documentId, currentUserId, currentUserRole);
            InputStreamResource resource = new InputStreamResource(fileStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document_unas_download\"")
                    .body(resource);
        } catch (DocumentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DocumentAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Atiende la petición DELETE para el borrado lógico del documento (RF-100 / RN-10)
    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivateDocument(
            @PathVariable("id") Long documentId,
            @RequestParam("userId") Integer currentUserId,
            @RequestParam("userRole") String currentUserRole) {
        try {
            deactivateDocumentUseCase.execute(documentId, currentUserId, currentUserRole);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } catch (DocumentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 si no existe
        } catch (DocumentAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Retorna 403 por permisos
        }
    }
}