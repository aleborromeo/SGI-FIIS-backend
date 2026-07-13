package com.sgi.fiis.proyectos.presentation.controller;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.proyectos.application.dto.CreateProjectRequest;
import com.sgi.fiis.proyectos.application.dto.ProjectResponse;
import com.sgi.fiis.proyectos.application.ports.in.CreateProjectUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import com.sgi.fiis.shared.domain.exception.BusinessRuleValidationException;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Endpoints for managing research projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ROLE_DOCENTE_INVESTIGADOR = "DOCENTE_INVESTIGADOR";

    private final CreateProjectUseCase createProjectUseCase;
    private final JdbcTemplate jdbcTemplate;

    public ProjectController(CreateProjectUseCase createProjectUseCase, JdbcTemplate jdbcTemplate) {
        this.createProjectUseCase = createProjectUseCase;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping
    @Operation(summary = "Postulate a new research project or save as draft")
    @ApiResponse(responseCode = "200", description = "Project successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid project request or business rule validation error")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        String role = currentUser.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace(ROLE_PREFIX, ""))
                .orElse("");
        
        if (!ROLE_DOCENTE_INVESTIGADOR.equals(role)) {
            throw new BusinessRuleValidationException("Solo los docentes investigadores pueden registrar proyectos de investigación.");
        }

        // RF-39: Associate the project with the logged-in user as the responsible investigator
        request.setResponsibleId(currentUser.getId().intValue());
        
        ProjectResponse response = createProjectUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List research projects with optional filters")
    @ApiResponse(responseCode = "200", description = "List of projects retrieved successfully")
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @RequestParam(value = "responsibleId", required = false) Long responsibleId,
            @RequestParam(value = "groupId", required = false) Integer groupId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        String role = currentUser.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace(ROLE_PREFIX, ""))
                .orElse("");

        List<ProjectResponse> response;

        if (ROLE_DOCENTE_INVESTIGADOR.equals(role)) {
            response = createProjectUseCase.getProjectsByResponsible(currentUser.getId());
        } else if ("COORDINADOR_GRUPO".equals(role)) {
            java.util.List<Integer> ids = jdbcTemplate.queryForList(
                "SELECT id_grupo FROM grupos_investigacion WHERE id_coordinador_actual = ? AND es_activo = TRUE LIMIT 1",
                Integer.class,
                currentUser.getId()
            );
            Integer coordGroupId = ids.isEmpty() ? null : ids.get(0);
            if (coordGroupId != null) {
                response = createProjectUseCase.getProjectsByGroup(coordGroupId);
            } else {
                response = List.of();
            }
        } else if ("ESTUDIANTE".equals(role)) {
            response = List.of();
        } else if (responsibleId != null) {
            response = createProjectUseCase.getProjectsByResponsible(responsibleId);
        } else if (groupId != null) {
            response = createProjectUseCase.getProjectsByGroup(groupId);
        } else {
            response = createProjectUseCase.getAllProjects();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/drafts")
    @Operation(summary = "List draft projects for the authenticated user")
    @ApiResponse(responseCode = "200", description = "List of draft projects")
    public ResponseEntity<List<ProjectResponse>> getMyDrafts(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        String role = currentUser.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace(ROLE_PREFIX, ""))
                .orElse("");
        if (!ROLE_DOCENTE_INVESTIGADOR.equals(role)) {
            return ResponseEntity.ok(List.of());
        }
        List<ProjectResponse> allDrafts = createProjectUseCase.getDraftsByResponsible(currentUser.getId());
        return ResponseEntity.ok(allDrafts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project details by ID")
    @ApiResponse(responseCode = "200", description = "Details retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - not your project")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        ProjectResponse project = createProjectUseCase.getProjectById(id);

        String role = currentUser.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace(ROLE_PREFIX, ""))
                .orElse("");

        if (ROLE_DOCENTE_INVESTIGADOR.equals(role) && (project.getResponsibleId() == null || !project.getResponsibleId().equals(currentUser.getId()))) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(project);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'COORDINADOR_GRUPO')")
    @Operation(summary = "Update project status")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status value")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ProjectResponse> updateProjectStatus(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        ProjectResponse response = createProjectUseCase.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a draft project")
    @ApiResponse(responseCode = "200", description = "Draft deleted successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<Void> deleteDraft(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        String role = currentUser.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace(ROLE_PREFIX, ""))
                .orElse("");
        if (!ROLE_DOCENTE_INVESTIGADOR.equals(role)) {
            return ResponseEntity.status(403).build();
        }
        createProjectUseCase.deleteDraft(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }
}
