package com.sgi.fiis.convocatorias.infrastructure.web;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.in.CreateCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.GetCallUseCase;
import com.sgi.fiis.convocatorias.application.ports.in.UpdateCallStatusUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    public ResearchCallController(CreateCallUseCase createCallUseCase,
                                  GetCallUseCase getCallUseCase,
                                  UpdateCallStatusUseCase updateCallStatusUseCase) {
        this.createCallUseCase = createCallUseCase;
        this.getCallUseCase = getCallUseCase;
        this.updateCallStatusUseCase = updateCallStatusUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION')")
    @Operation(summary = "Create a new research call", description = "Allows the research director to register a new research call with submission date ranges.")
    @ApiResponse(responseCode = "200", description = "Research call successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires DIRECTOR_INVESTIGACION role")
    public ResponseEntity<CallResponse> createCall(@Valid @RequestBody CreateCallRequest request) {
        CallResponse response = createCallUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE_INVESTIGADOR', 'DIRECTOR_INVESTIGACION', 'ADMIN')")
    @Operation(summary = "List research calls", description = "Retrieves all research calls, optionally filtered by status.")
    @ApiResponse(responseCode = "200", description = "List of research calls retrieved successfully")
    public ResponseEntity<List<CallResponse>> getCalls(
            @RequestParam(value = "status", required = false) String status) {
        List<CallResponse> calls = getCallUseCase.getCalls(status);
        return ResponseEntity.ok(calls);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE_INVESTIGADOR', 'DIRECTOR_INVESTIGACION', 'ADMIN')")
    @Operation(summary = "Get research call by ID", description = "Retrieves a single research call by its ID.")
    @ApiResponse(responseCode = "200", description = "Research call found")
    @ApiResponse(responseCode = "404", description = "Research call not found")
    public ResponseEntity<CallResponse> getCallById(@PathVariable("id") Integer id) {
        CallResponse call = getCallUseCase.getCallById(id);
        return ResponseEntity.ok(call);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION')")
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
