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

    private final CreateProjectUseCase createProjectUseCase;
    private final JdbcTemplate jdbcTemplate;

    public ProjectController(CreateProjectUseCase createProjectUseCase, JdbcTemplate jdbcTemplate) {
        this.createProjectUseCase = createProjectUseCase;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping
    @Operation(summary = "Postulate a new research project")
    @ApiResponse(responseCode = "200", description = "Project successfully postulated")
    @ApiResponse(responseCode = "400", description = "Invalid project request or business rule validation error")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        String role = currentUser.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");
        
        if (!"DOCENTE_INVESTIGADOR".equals(role)) {
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
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");

        List<ProjectResponse> response;

        // RF-43 / RN-05 / RNF-19: Enforce role-based filtering
        if ("DOCENTE_INVESTIGADOR".equals(role)) {
            // Docente can only see their own projects
            response = createProjectUseCase.getProjectsByResponsible(currentUser.getId());
        } else if ("COORDINADOR_GRUPO".equals(role)) {
            // Coordinador can only see projects of the research group they coordinate
            Integer coordGroupId = jdbcTemplate.query(
                "SELECT id_grupo FROM grupos_investigacion WHERE id_coordinador_actual = ? AND es_activo = TRUE LIMIT 1",
                rs -> rs.next() ? rs.getObject("id_grupo", Integer.class) : null,
                currentUser.getId()
            );
            if (coordGroupId != null) {
                response = createProjectUseCase.getProjectsByGroup(coordGroupId);
            } else {
                response = List.of();
            }
        } else if ("ESTUDIANTE".equals(role)) {
            // Students do not coordinate or lead research projects
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

    @GetMapping("/{id}")
    @Operation(summary = "Get project details by ID")
    @ApiResponse(responseCode = "200", description = "Details retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - not your project")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        ProjectResponse project = createProjectUseCase.getProjectById(id);

        // RF-43 / RN-05: Docentes can only see their own projects
        String role = currentUser.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");

        if ("DOCENTE_INVESTIGADOR".equals(role) && (project.getResponsibleId() == null || !project.getResponsibleId().equals(currentUser.getId()))) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(project);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'COORDINADOR_GRUPO')")
    @Operation(summary = "Update project status", description = "Updates the status of a research project.")
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

}
