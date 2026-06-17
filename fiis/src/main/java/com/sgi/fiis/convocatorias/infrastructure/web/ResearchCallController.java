package com.sgi.fiis.convocatorias.infrastructure.web;

import com.sgi.fiis.convocatorias.application.dto.CallResponse;
import com.sgi.fiis.convocatorias.application.dto.CreateCallRequest;
import com.sgi.fiis.convocatorias.application.ports.in.CreateCallUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/calls")
@Tag(name = "Research Calls", description = "Endpoints for managing institutional research calls")
@SecurityRequirement(name = "bearerAuth")
public class ResearchCallController {

    private final CreateCallUseCase createCallUseCase;

    public ResearchCallController(CreateCallUseCase createCallUseCase) {
        this.createCallUseCase = createCallUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('DIRECTOR_INVESTIGACION')")
    @Operation(summary = "Create a new research call", description = "Allows the research director to register a new research call with submission date ranges.")
    @ApiResponse(responseCode = "200", description = "Research call successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires DIRECTOR_INVESTIGACION role")
    public ResponseEntity<CallResponse> createCall(@Valid @RequestBody CreateCallRequest request) {
        CallResponse response = createCallUseCase.execute(request);
        return ResponseEntity.ok(response);
    }
}
