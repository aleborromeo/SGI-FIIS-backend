package com.sgi.fiis.shared.presentation.controller;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.domain.exception.FileStorageException;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentEntity;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentJpaRepository;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Map;
import java.util.Optional;

/**
 * @deprecated Use DocumentController (/api/documents) instead
 */
@Deprecated(since = "2.0.0", forRemoval = true)
@SuppressWarnings("java:S1133")
@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Files", description = "DEPRECATED: Use DocumentController (/api/documents) instead")
@SecurityRequirement(name = "bearerAuth")
public class FileController {

    private final DocumentJpaRepository documentRepository;
    private final ProjectJpaRepository projectRepository;
    private final GroupMembershipJpaRepository membershipRepository;

    @Deprecated(since = "2.0.0", forRemoval = true)
    public FileController(DocumentJpaRepository documentRepository,
                          ProjectJpaRepository projectRepository,
                          GroupMembershipJpaRepository membershipRepository) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
        this.membershipRepository = membershipRepository;
    }

    /**
     * @deprecated Upload is no longer supported. Use DocumentController (/api/documents) instead.
     */
    @Deprecated(since = "2.1.0", forRemoval = true)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload proposal document [DEPRECATED]", description = "This endpoint is deprecated. Use DocumentController instead.", deprecated = true)
    @ApiResponse(responseCode = "410", description = "Gone - Use DocumentController (/api/documents) instead")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        return ResponseEntity.status(HttpStatus.GONE).build();
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Download file by ID", description = "Validates access rights before download.")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Unauthorized to download this file")
    @ApiResponse(responseCode = "404", description = "File not found")
    @Deprecated(since = "2.0.0", forRemoval = true)
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleValidationException("Document not found with ID: " + id));

        if (!isAuthorizedToDownload(document, id, currentUser)) {
            return ResponseEntity.status(403).build();
        }

        try {
            Path filePath = Paths.get(document.getStoragePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new BusinessRuleValidationException("File not found on disk.");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(resolveContentType(document.getFileExtension())))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new FileStorageException("Error reading physical file", e);
        }
    }

    // --- Private helpers to reduce cyclomatic complexity (JAVA-R1000) ---

    private boolean isAuthorizedToDownload(DocumentEntity document, Integer documentId, CustomUserDetails currentUser) {
        if (isPrivilegedRole(currentUser)) {
            return true;
        }
        if (document.getCreatorId().equals(currentUser.getId())) {
            return true;
        }
        return isOwnerOrCoordinatorOfLinkedProject(documentId, currentUser);
    }

    private boolean isPrivilegedRole(CustomUserDetails currentUser) {
        return currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_DIRECTOR_INVESTIGACION")
                        || a.getAuthority().equals("ROLE_DECANO"));
    }

    private boolean isOwnerOrCoordinatorOfLinkedProject(Integer documentId, CustomUserDetails currentUser) {
        Optional<ProjectEntity> projectOpt = projectRepository.findByDocumentId(documentId);
        if (projectOpt.isEmpty()) {
            return false;
        }
        ProjectEntity project = projectOpt.get();
        if (project.getResponsible().getId().equals(currentUser.getId())) {
            return true;
        }
        return isGroupCoordinatorOfProject(project, currentUser);
    }

    private boolean isGroupCoordinatorOfProject(ProjectEntity project, CustomUserDetails currentUser) {
        boolean isCoordinator = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDINADOR_GRUPO"));
        if (!isCoordinator) {
            return false;
        }
        Optional<GroupMembershipEntity> membershipOpt = membershipRepository.findByUserIdAndActiveTrue(currentUser.getId());
        return membershipOpt.isPresent()
                && membershipOpt.get().getGroup().getId().equals(project.getGroup().getId());
    }

    private String resolveContentType(String extension) {
        if (extension.equalsIgnoreCase("PDF")) {
            return "application/pdf";
        } else if (extension.equalsIgnoreCase("DOC")) {
            return "application/msword";
        } else if (extension.equalsIgnoreCase("DOCX")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/octet-stream";
    }
}

