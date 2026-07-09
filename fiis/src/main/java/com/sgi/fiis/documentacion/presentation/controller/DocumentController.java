package com.sgi.fiis.documentacion.presentation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.sgi.fiis.documentacion.application.dto.DocumentResponseDto;
import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.documentacion.application.usecase.DeactivateDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.DownloadDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.ListDocumentsUseCase;
import com.sgi.fiis.documentacion.application.usecase.UploadDocumentUseCase;
import com.sgi.fiis.documentacion.application.usecase.DocumentDownloadResult;
import com.sgi.fiis.documentacion.application.exception.DocumentAccessDeniedException;
import com.sgi.fiis.documentacion.application.exception.DocumentNotFoundException;

import java.io.IOException;

@RestController
@RequestMapping("/api/documents")
@PreAuthorize("isAuthenticated()")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final UploadDocumentUseCase uploadDocumentUseCase;
    private final DownloadDocumentUseCase downloadDocumentUseCase;
    private final DeactivateDocumentUseCase deactivateDocumentUseCase;
    private final ListDocumentsUseCase listDocumentsUseCase;

    public DocumentController(UploadDocumentUseCase uploadDocumentUseCase, 
                              DownloadDocumentUseCase downloadDocumentUseCase,
                              DeactivateDocumentUseCase deactivateDocumentUseCase,
                              ListDocumentsUseCase listDocumentsUseCase) {
        this.uploadDocumentUseCase = uploadDocumentUseCase;
        this.downloadDocumentUseCase = downloadDocumentUseCase;
        this.deactivateDocumentUseCase = deactivateDocumentUseCase;
        this.listDocumentsUseCase = listDocumentsUseCase;
    }

    // Endpoint para Carga de Documentos (RF-65)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        logAuthenticationDetails(authentication);
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equalsIgnoreCase("application/pdf")
                || contentType.equalsIgnoreCase("application/msword")
                || contentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            return ResponseEntity.badRequest().header("X-Error-Cause", "Tipo de archivo (MIME) no permitido. Solo se admite PDF, DOC o DOCX.").build();
        }

        try {
            Long userId = extractUserId(authentication);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

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
            Authentication authentication) {

        logAuthenticationDetails(authentication);

        try {
            Long userId = extractUserId(authentication);
            String role = extractRole(authentication);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            DocumentDownloadResult downloadResult = downloadDocumentUseCase.execute(documentId, userId, role);
            InputStreamResource resource = new InputStreamResource(downloadResult.getInputStream());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadResult.getOriginalName() + "\"")
                    .body(resource);
        } catch (DocumentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (DocumentAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // Endpoint para listar todos los documentos activos (M3)
    @GetMapping
    public ResponseEntity<java.util.List<DocumentResponseDto>> listDocuments() {
        return ResponseEntity.ok(listDocumentsUseCase.execute());
    }

    // Atiende la petición DELETE para el borrado lógico del documento (RF-100 / RN-10)
    @DeleteMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivateDocument(
            @PathVariable("id") Long documentId,
            Authentication authentication) {
        
        logAuthenticationDetails(authentication);

        try {
            Long userId = extractUserId(authentication);
            String role = extractRole(authentication);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            deactivateDocumentUseCase.execute(documentId, userId, role);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content
        } catch (DocumentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retorna 404 si no existe
        } catch (DocumentAccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Retorna 403 por permisos
        }
    }

    // --- Métodos auxiliares para extraer identidad del contexto de seguridad ---

    private void logAuthenticationDetails(Authentication authentication) {
        log.info("[DOCUMENTOS-AUTH-CHECK] ¿Petición Autenticada?: {}", authentication != null);
        if (authentication != null) {
            log.info("[DOCUMENTOS-AUTH-CHECK] Usuario (Subject): {}", authentication.getName());
            log.info("[DOCUMENTOS-AUTH-CHECK] Autorizaciones / Roles: {}", authentication.getAuthorities());
            Long userId = extractUserId(authentication);
            log.info("[DOCUMENTOS-AUTH-CHECK] ID de usuario resuelto: {}", userId);
        }
    }

    /**
     * Extrae el ID del usuario autenticado desde el principal del token JWT de forma directa y segura.
     */
    private Long extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        return null;
    }

    /**
     * Extrae el rol principal del usuario autenticado.
     * Stripea el prefijo "ROLE_" si existe, ya que los use cases comparan
     * contra nombres sin prefijo (ej: "ADMIN", "DECANO").
     */
    private String extractRole(Authentication authentication) {
        GrantedAuthority authority = authentication.getAuthorities().iterator().next();
        String role = authority.getAuthority();
        if (role.startsWith("ROLE_")) {
            return role.substring(5);
        }
        return role;
    }
}