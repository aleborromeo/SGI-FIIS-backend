package com.sgi.fiis.shared.presentation.controller;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Files", description = "Endpoints for uploading and downloading documents")
@SecurityRequirement(name = "bearerAuth")
public class FileController {

    private final DocumentJpaRepository documentRepository;
    private final ProjectJpaRepository projectRepository;
    private final GroupMembershipJpaRepository membershipRepository;
    private final String uploadDir;

    public FileController(DocumentJpaRepository documentRepository,
                          ProjectJpaRepository projectRepository,
                          GroupMembershipJpaRepository membershipRepository,
                          @Value("${app.upload-dir:/app/uploads}") String uploadDir) {
        this.documentRepository = documentRepository;
        this.projectRepository = projectRepository;
        this.membershipRepository = membershipRepository;
        this.uploadDir = uploadDir;

        // Ensure upload directory exists
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload proposal document", description = "Only allows PDF, DOC, and DOCX files up to 10 MB.")
    @ApiResponse(responseCode = "200", description = "File successfully uploaded")
    @ApiResponse(responseCode = "400", description = "Invalid file extension or size exceeds 10 MB")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        if (file.isEmpty()) {
            throw new BusinessRuleValidationException("File is empty.");
        }

        // 1. Check file size (max 10 MB = 10 * 1024 * 1024 bytes)
        long maxSize = 10L * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BusinessRuleValidationException("File size exceeds the 10 MB limit.");
        }

        // 2. Check extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessRuleValidationException("Filename is invalid.");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex + 1).toUpperCase();
        }

        if (!extension.equals("PDF") && !extension.equals("DOC") && !extension.equals("DOCX")) {
            throw new BusinessRuleValidationException("Only PDF, DOC, and DOCX files are allowed.");
        }

        // 3. Save physical file to target directory
        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path targetPath = Paths.get(uploadDir).resolve(savedFilename).toAbsolutePath();

        try {
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save physical file", e);
        }

        // 4. Save metadata to DB
        DocumentEntity document = DocumentEntity.builder()
                .originalName(originalFilename)
                .storagePath(targetPath.toString())
                .sizeBytes(file.getSize())
                .fileExtension(extension)
                .creatorId(currentUser.getId())
                .build();

        DocumentEntity savedDoc = documentRepository.save(document);

        return ResponseEntity.ok(Map.of(
                "id", savedDoc.getId(),
                "originalName", savedDoc.getOriginalName(),
                "extension", savedDoc.getFileExtension()
        ));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Download file by ID", description = "Validates access rights before download.")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Unauthorized to download this file")
    @ApiResponse(responseCode = "404", description = "File not found")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        DocumentEntity document = documentRepository.findById(id)
                .orElseThrow(() -> new BusinessRuleValidationException("Document not found with ID: " + id));

        // Security check
        boolean isAuthorized = false;

        // Check Roles
        boolean isAdminOrDirectorOrDecano = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") ||
                               a.getAuthority().equals("ROLE_DIRECTOR_INVESTIGACION") ||
                               a.getAuthority().equals("ROLE_DECANO"));

        if (isAdminOrDirectorOrDecano) {
            isAuthorized = true;
        } else if (document.getCreatorId().equals(currentUser.getId())) {
            isAuthorized = true;
        } else {
            // Check if document is linked to a project where the user is responsible or group coordinator
            Optional<ProjectEntity> projectOpt = projectRepository.findByDocumentId(id);
            if (projectOpt.isPresent()) {
                ProjectEntity project = projectOpt.get();
                if (project.getResponsible().getId().equals(currentUser.getId())) {
                    isAuthorized = true;
                } else {
                    boolean isCoordinator = currentUser.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_COORDINADOR_GRUPO"));
                    if (isCoordinator) {
                        Optional<GroupMembershipEntity> membershipOpt = membershipRepository.findByUserIdAndActiveTrue(currentUser.getId());
                        if (membershipOpt.isPresent() && membershipOpt.get().getGroup().getId().equals(project.getGroup().getId())) {
                            isAuthorized = true;
                        }
                    }
                }
            }
        }

        if (!isAuthorized) {
            return ResponseEntity.status(403).build();
        }

        try {
            Path filePath = Paths.get(document.getStoragePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = "application/octet-stream";
                if (document.getFileExtension().equalsIgnoreCase("PDF")) {
                    contentType = "application/pdf";
                } else if (document.getFileExtension().equalsIgnoreCase("DOC")) {
                    contentType = "application/msword";
                } else if (document.getFileExtension().equalsIgnoreCase("DOCX")) {
                    contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalName() + "\"")
                        .body(resource);
            } else {
                throw new BusinessRuleValidationException("File not found on disk.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error reading physical file", e);
        }
    }
}
