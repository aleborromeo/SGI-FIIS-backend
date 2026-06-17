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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projects", description = "Endpoints for managing research projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;

    public ProjectController(CreateProjectUseCase createProjectUseCase) {
        this.createProjectUseCase = createProjectUseCase;
    }

    @PostMapping
    @Operation(summary = "Postulate a new research project")
    @ApiResponse(responseCode = "200", description = "Project successfully postulated")
    @ApiResponse(responseCode = "400", description = "Invalid project request or business rule validation error")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        
        // Associate the project with the logged-in user as the responsible investigator
        request.setResponsibleId(currentUser.getId().intValue());
        
        ProjectResponse response = createProjectUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List research projects with optional filters")
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @RequestParam(value = "responsibleId", required = false) Integer responsibleId,
            @RequestParam(value = "groupId", required = false) Integer groupId) {
        
        List<ProjectResponse> response;
        if (responsibleId != null) {
            response = createProjectUseCase.getProjectsByResponsible(responsibleId.longValue());
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
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable("id") Integer id) {
        ProjectResponse response = createProjectUseCase.getProjectById(id);
        return ResponseEntity.ok(response);
    }
}