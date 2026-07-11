package com.sgi.fiis.convocatorias.infrastructure.web;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.dto.PrerequisitosResponse;
import com.sgi.fiis.convocatorias.application.ports.in.CreateCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.GetCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.UpdateCallStatusUseCase;
import com.sgi.fiis.grupos_investigacion.domain.port.MembershipRepositoryPort;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/calls")
@Tag(name = "Research Calls", description = "Endpoints for managing institutional research calls")
@SecurityRequirement(name = "bearerAuth")
public class ResearchCallController {

    private final CreateCallUseCase createCallUseCase;
    private final GetCallUseCase getCallUseCase;
    private final UpdateCallStatusUseCase updateCallStatusUseCase;
    private final MembershipRepositoryPort membershipRepositoryPort;

    public ResearchCallController(CreateCallUseCase createCallUseCase,
                                  GetCallUseCase getCallUseCase,
                                  UpdateCallStatusUseCase updateCallStatusUseCase,
                                  MembershipRepositoryPort membershipRepositoryPort) {
        this.createCallUseCase = createCallUseCase;
        this.getCallUseCase = getCallUseCase;
        this.updateCallStatusUseCase = updateCallStatusUseCase;
        this.membershipRepositoryPort = membershipRepositoryPort;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'ADMIN')")
    @Operation(summary = "Create a new research call", description = "Allows the research director to register a new research call with submission date ranges.")
    @ApiResponse(responseCode = "200", description = "Research call successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires DIRECTOR_INVESTIGACION role")
    public ResponseEntity<CallResponse> createCall(
            @Valid @RequestBody CreateCallRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        CallResponse response = createCallUseCase.execute(request, currentUser.getId().intValue());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List research calls", description = "Retrieves all research calls, optionally filtered by status.")
    @ApiResponse(responseCode = "200", description = "List of research calls retrieved successfully")
    public ResponseEntity<List<CallResponse>> getCalls(
            @RequestParam(value = "status", required = false) String status) {
        List<CallResponse> calls = getCallUseCase.getCalls(status);
        return ResponseEntity.ok(calls);
    }

    @GetMapping("/vigent")
    @Operation(summary = "Get open/vigent research calls", description = "Retrieves all research calls with OPEN status.")
    @ApiResponse(responseCode = "200", description = "List of vigent calls retrieved successfully")
    public ResponseEntity<List<CallResponse>> getVigentCalls() {
        List<CallResponse> calls = getCallUseCase.getVigentCalls();
        return ResponseEntity.ok(calls);
    }

    @GetMapping("/prerequisitos")
    @Operation(summary = "Check user prerequisites", description = "Checks if the authenticated user meets prerequisites for project submission.")
    @ApiResponse(responseCode = "200", description = "Prerequisites check result")
    public ResponseEntity<PrerequisitosResponse> checkPrerequisitos(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        boolean hasActiveGroup = membershipRepositoryPort.existsActiveByUser(currentUser.getId().intValue());
        boolean hasVigentCalls = !getCallUseCase.getVigentCalls().isEmpty();
        boolean isDocente = currentUser.getAuthorities().stream()
                .anyMatch(a -> "ROLE_DOCENTE_INVESTIGADOR".equals(a.getAuthority()));
        boolean valid = hasActiveGroup && hasVigentCalls && isDocente;
        return ResponseEntity.ok(PrerequisitosResponse.builder()
                .hasActiveGroup(hasActiveGroup)
                .hasVigentCalls(hasVigentCalls)
                .docente(isDocente)
                .valid(valid)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get research call by ID", description = "Retrieves a single research call by its ID.")
    @ApiResponse(responseCode = "200", description = "Research call found")
    @ApiResponse(responseCode = "404", description = "Research call not found")
    public ResponseEntity<CallResponse> getCallById(@PathVariable("id") Integer id) {
        CallResponse call = getCallUseCase.getCallById(id);
        return ResponseEntity.ok(call);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'ADMIN')")
    @Operation(summary = "Update research call status", description = "Allows the research director to change the status of a research call (ABIERTA, CERRADA, FINALIZADA).")
    @ApiResponse(responseCode = "200", description = "Status updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid status value")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires DIRECTOR_INVESTIGACION role")
    @ApiResponse(responseCode = "404", description = "Research call not found")
    public ResponseEntity<CallResponse> updateCallStatus(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        CallResponse response = updateCallStatusUseCase.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }

}
