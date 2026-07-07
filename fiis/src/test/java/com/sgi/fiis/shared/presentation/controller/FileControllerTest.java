package com.sgi.fiis.shared.presentation.controller;

import com.sgi.fiis.shared.infrastructure.persistence.DocumentJpaRepository;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipJpaRepository;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.GroupMembershipEntity;
import com.sgi.fiis.grupos_investigacion.infrastructure.persistence.ResearchGroupEntity;
import com.sgi.fiis.proyectos.infrastructure.persistence.ProjectEntity;
import com.sgi.fiis.users.infrastructure.persistence.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;
import com.sgi.fiis.shared.infrastructure.persistence.DocumentEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private DocumentJpaRepository documentRepository;
    private ProjectJpaRepository projectRepository;
    private GroupMembershipJpaRepository membershipRepository;
    private FileController fileController;
    private Path tempUploadDir;

    @BeforeEach
    void setup() throws IOException {
        documentRepository = Mockito.mock(DocumentJpaRepository.class);
        projectRepository = Mockito.mock(ProjectJpaRepository.class);
        membershipRepository = Mockito.mock(GroupMembershipJpaRepository.class);
        tempUploadDir = Files.createTempDirectory("test-uploads");
        fileController = new FileController(documentRepository, projectRepository, membershipRepository,
                tempUploadDir.toString());
    }

    @Test
    void shouldUploadFileSuccessfully() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf",
                "dummy content".getBytes());
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.emptyList());

        DocumentEntity savedDoc = DocumentEntity.builder()
                .id(1)
                .originalName("test.pdf")
                .fileExtension("PDF")
                .build();
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(savedDoc);

        ResponseEntity<Map<String, Object>> response = fileController.uploadFile(file, userDetails);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("test.pdf", response.getBody().get("originalName"));
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", new byte[0]);
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.emptyList());

        assertThrows(BusinessRuleValidationException.class, () -> fileController.uploadFile(file, userDetails));
    }

    @Test
    void shouldThrowExceptionWhenFileIsTooLarge() {
        MockMultipartFile file = Mockito.mock(MockMultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(15L * 1024 * 1024);

        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.emptyList());

        assertThrows(BusinessRuleValidationException.class, () -> fileController.uploadFile(file, userDetails));
    }

    @Test
    void shouldThrowExceptionForInvalidExtension() {
        MockMultipartFile file = new MockMultipartFile("file", "test.exe", "application/octet-stream",
                "dummy content".getBytes());
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.emptyList());

        assertThrows(BusinessRuleValidationException.class, () -> fileController.uploadFile(file, userDetails));
    }
    
    @Test
    void shouldThrowExceptionWhenOriginalFilenameIsNull() {
        MockMultipartFile file = new MockMultipartFile("file", null, "application/pdf", "dummy".getBytes());
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.emptyList());

        assertThrows(BusinessRuleValidationException.class, () -> fileController.uploadFile(file, userDetails));
    }
    
    @Test
    void shouldThrowExceptionWhenTransferFails() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(100L);
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        Mockito.doThrow(new IOException("Disk full")).when(file).transferTo(any(java.io.File.class));

        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.emptyList());

        assertThrows(RuntimeException.class, () -> fileController.uploadFile(file, userDetails));
    }

    @Test
    void shouldDownloadFileSuccessfully() throws IOException {
        Path tempFile = Files.createTempFile("test", ".pdf");
        Files.write(tempFile, "dummy content".getBytes());

        DocumentEntity doc = DocumentEntity.builder()
                .id(1)
                .originalName("test.pdf")
                .fileExtension("PDF")
                .storagePath(tempFile.toString())
                .creatorId(1L)
                .build();

        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        ResponseEntity<org.springframework.core.io.Resource> response = fileController.downloadFile(1, userDetails);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("test.pdf"));
        assertEquals("application/pdf", response.getHeaders().getContentType().toString());

        Files.deleteIfExists(tempFile);
    }
    
    @Test
    void shouldDownloadDocAndDocx() throws IOException {
        Path tempFile = Files.createTempFile("test", ".doc");
        Files.write(tempFile, "dummy content".getBytes());

        DocumentEntity doc1 = DocumentEntity.builder().id(1).originalName("test.doc").fileExtension("DOC").storagePath(tempFile.toString()).creatorId(1L).build();
        DocumentEntity doc2 = DocumentEntity.builder().id(2).originalName("test.docx").fileExtension("DOCX").storagePath(tempFile.toString()).creatorId(1L).build();
        DocumentEntity doc3 = DocumentEntity.builder().id(3).originalName("test.txt").fileExtension("TXT").storagePath(tempFile.toString()).creatorId(1L).build();

        when(documentRepository.findById(1)).thenReturn(Optional.of(doc1));
        when(documentRepository.findById(2)).thenReturn(Optional.of(doc2));
        when(documentRepository.findById(3)).thenReturn(Optional.of(doc3));
        
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true, Collections.emptyList());

        assertEquals("application/msword", fileController.downloadFile(1, userDetails).getHeaders().getContentType().toString());
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileController.downloadFile(2, userDetails).getHeaders().getContentType().toString());
        assertEquals("application/octet-stream", fileController.downloadFile(3, userDetails).getHeaders().getContentType().toString());

        Files.deleteIfExists(tempFile);
    }

    @Test
    void shouldThrowExceptionWhenFileNotFoundOnDisk() {
        DocumentEntity doc = DocumentEntity.builder()
                .id(1)
                .originalName("test.pdf")
                .fileExtension("PDF")
                .storagePath("/tmp/nonexistent-file-12345.pdf")
                .creatorId(1L)
                .build();

        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.emptyList());

        assertThrows(BusinessRuleValidationException.class, () -> fileController.downloadFile(1, userDetails));
    }

    @Test
    void shouldForbidDownloadIfUnauthorized() {
        DocumentEntity doc = DocumentEntity.builder()
                .id(1)
                .originalName("test.pdf")
                .fileExtension("PDF")
                .storagePath("/tmp/test.pdf")
                .creatorId(2L) // Different creator
                .build();

        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));
        when(projectRepository.findByDocumentId(1)).thenReturn(Optional.empty()); // Not owner of project

        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "pass", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        ResponseEntity<org.springframework.core.io.Resource> response = fileController.downloadFile(1, userDetails);
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void shouldAllowDownloadForAdmin() throws IOException {
        Path tempFile = Files.createTempFile("test", ".pdf");
        Files.write(tempFile, "dummy content".getBytes());

        DocumentEntity doc = DocumentEntity.builder()
                .id(1)
                .originalName("test.pdf")
                .fileExtension("PDF")
                .storagePath(tempFile.toString())
                .creatorId(2L) // Different creator
                .build();

        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));
        CustomUserDetails userDetails = new CustomUserDetails(1L, "admin@test.com", "pass", true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        ResponseEntity<org.springframework.core.io.Resource> response = fileController.downloadFile(1, userDetails);
        assertEquals(200, response.getStatusCode().value());

        Files.deleteIfExists(tempFile);
    }
    
    @Test
    void shouldAllowDownloadForProjectOwner() throws IOException {
        Path tempFile = Files.createTempFile("test", ".pdf");
        Files.write(tempFile, "dummy".getBytes());

        DocumentEntity doc = DocumentEntity.builder().id(1).originalName("t.pdf").fileExtension("PDF").storagePath(tempFile.toString()).creatorId(2L).build();
        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));
        
        ProjectEntity proj = new ProjectEntity();
        UserEntity owner = new UserEntity();
        owner.setId(1L);
        proj.setResponsible(owner);
        when(projectRepository.findByDocumentId(1)).thenReturn(Optional.of(proj));

        CustomUserDetails userDetails = new CustomUserDetails(1L, "owner@test.com", "pass", true, Collections.emptyList());
        ResponseEntity<org.springframework.core.io.Resource> response = fileController.downloadFile(1, userDetails);
        
        assertEquals(200, response.getStatusCode().value());
        Files.deleteIfExists(tempFile);
    }
    
    @Test
    void shouldAllowDownloadForProjectCoordinator() throws IOException {
        Path tempFile = Files.createTempFile("test", ".pdf");
        Files.write(tempFile, "dummy".getBytes());

        DocumentEntity doc = DocumentEntity.builder().id(1).originalName("t.pdf").fileExtension("PDF").storagePath(tempFile.toString()).creatorId(2L).build();
        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));
        
        ProjectEntity proj = new ProjectEntity();
        UserEntity owner = new UserEntity();
        owner.setId(99L);
        proj.setResponsible(owner);
        
        ResearchGroupEntity group = new ResearchGroupEntity();
        group.setId(5);
        proj.setGroup(group);
        when(projectRepository.findByDocumentId(1)).thenReturn(Optional.of(proj));
        
        GroupMembershipEntity membership = new GroupMembershipEntity();
        membership.setGroup(group);
        when(membershipRepository.findByUserIdAndActiveTrue(1L)).thenReturn(Optional.of(membership));

        CustomUserDetails userDetails = new CustomUserDetails(1L, "coord@test.com", "pass", true, Collections.singletonList(new SimpleGrantedAuthority("ROLE_COORDINADOR_GRUPO")));
        ResponseEntity<org.springframework.core.io.Resource> response = fileController.downloadFile(1, userDetails);
        
        assertEquals(200, response.getStatusCode().value());
        Files.deleteIfExists(tempFile);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempUploadDir != null) {
            // Clean up all files in temp dir first
            try (var files = Files.list(tempUploadDir)) {
                files.forEach(file -> {
                    try {
                        Files.deleteIfExists(file);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
            }
            Files.deleteIfExists(tempUploadDir);
        }
    }
}
