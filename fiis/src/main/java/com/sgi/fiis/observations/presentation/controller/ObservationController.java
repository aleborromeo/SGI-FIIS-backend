package com.sgi.fiis.observations.presentation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sgi.fiis.observations.application.dto.ObservationRequestDTO;
import com.sgi.fiis.observations.application.dto.ObservationResponseDTO;
import com.sgi.fiis.observations.application.dto.RemedyRequestDTO;
import com.sgi.fiis.observations.application.dto.RemedyResponseDTO;
import com.sgi.fiis.observations.application.usecase.GetObservationUseCase;
import com.sgi.fiis.observations.application.usecase.ListObservationsByProcedureUseCase;
import com.sgi.fiis.observations.application.usecase.ListRemediesByObservationUseCase;
import com.sgi.fiis.observations.application.usecase.RegisterObservationUseCase;
import com.sgi.fiis.observations.application.usecase.RegisterRemedyUseCase;
import com.sgi.fiis.observations.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observations.domain.exception.InvalidRemedyException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing procedure observations and remedies.
 */
@RestController
@RequestMapping("/api/observations")
@RequiredArgsConstructor
@Tag(name = "Observations", description = "Endpoints for managing procedure observations and their remedies")
@SecurityRequirement(name = "bearerAuth")
public class ObservationController {

    private final RegisterObservationUseCase registerObservationUseCase;
    private final RegisterRemedyUseCase registerRemedyUseCase;
    private final ListObservationsByProcedureUseCase listObservationsByProcedureUseCase;
    private final GetObservationUseCase getObservationUseCase;
    private final ListRemediesByObservationUseCase listRemediesByObservationUseCase;

    private static final String ERROR_KEY = "error";

    /**
     * Registers a new observation on a procedure.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION', 'DECANO')")
    @Operation(summary = "Register a new observation", description = "Registers an observation on a procedure (Restricted to Reviewers and Admins).")
    @ApiResponse(responseCode = "201", description = "Observation registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request payload")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires Reviewer Role")
    public ResponseEntity<ObservationResponseDTO> registerObservation(
            @RequestBody ObservationRequestDTO dto) {
        ObservationResponseDTO response = registerObservationUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Registers a remedy to address a pending observation.
     */
    @PostMapping("/{id}/remedy")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE_INVESTIGADOR', 'ESTUDIANTE')")
    @Operation(summary = "Register a remedy for an observation", description = "Allows the applicant to resolve a pending observation by providing a remedy.")
    @ApiResponse(responseCode = "201", description = "Remedy registered successfully")
    @ApiResponse(responseCode = "403", description = "Forbidden - Requires Applicant Role")
    @ApiResponse(responseCode = "404", description = "Observation not found")
    @ApiResponse(responseCode = "409", description = "Conflict - Invalid remedy operation")
    public ResponseEntity<RemedyResponseDTO> registerRemedy(
            @PathVariable Integer id,
            @RequestBody RemedyRequestDTO dto) {
        RemedyResponseDTO response = registerRemedyUseCase.execute(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lists all observations of a procedure.
     */
    @GetMapping("/procedure/{procedureId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List observations by procedure", description = "Retrieves all observations associated with a specific procedure.")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    public ResponseEntity<List<ObservationResponseDTO>> listByProcedure(
            @PathVariable Integer procedureId) {
        List<ObservationResponseDTO> response = listObservationsByProcedureUseCase.execute(procedureId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the complete details of an observation.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get observation by ID", description = "Retrieves details of a specific observation.")
    @ApiResponse(responseCode = "200", description = "Observation found")
    @ApiResponse(responseCode = "404", description = "Observation not found")
    public ResponseEntity<ObservationResponseDTO> getObservation(
            @PathVariable Integer id) {
        ObservationResponseDTO response = getObservationUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lists all remedies of a specific observation.
     */
    @GetMapping("/{id}/remedies")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List remedies by observation", description = "Retrieves all remedies submitted for a specific observation.")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    public ResponseEntity<List<RemedyResponseDTO>> listRemedies(
            @PathVariable Integer id) {
        List<RemedyResponseDTO> response = listRemediesByObservationUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ObservationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ObservationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(ERROR_KEY, ex.getMessage()));
    }

    @ExceptionHandler(InvalidRemedyException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRemedy(InvalidRemedyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(ERROR_KEY, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(ERROR_KEY, ex.getMessage()));
    }
}
