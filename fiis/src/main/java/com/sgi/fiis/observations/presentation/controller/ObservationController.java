package com.sgi.fiis.observations.presentation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for managing procedure observations and remedies.
 */
@RestController
@RequestMapping("/api/observations")
@RequiredArgsConstructor
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
    public ResponseEntity<ObservationResponseDTO> registerObservation(
            @RequestBody ObservationRequestDTO dto) {
        ObservationResponseDTO response = registerObservationUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Registers a remedy to address a pending observation.
     */
    @PostMapping("/{id}/remedy")
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
    public ResponseEntity<List<ObservationResponseDTO>> listByProcedure(
            @PathVariable Integer procedureId) {
        List<ObservationResponseDTO> response = listObservationsByProcedureUseCase.execute(procedureId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the complete details of an observation.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ObservationResponseDTO> getObservation(
            @PathVariable Integer id) {
        ObservationResponseDTO response = getObservationUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lists all remedies of a specific observation.
     */
    @GetMapping("/{id}/remedies")
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
