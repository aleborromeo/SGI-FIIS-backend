package com.sgi.fiis.observaciones.presentation.controller;

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

import com.sgi.fiis.observaciones.application.dto.ObservationRequestDto;
import com.sgi.fiis.observaciones.application.dto.ObservationResponseDto;
import com.sgi.fiis.observaciones.application.dto.CorrectionRequestDto;
import com.sgi.fiis.observaciones.application.dto.CorrectionResponseDto;
import com.sgi.fiis.observaciones.application.usecase.GetObservationUseCase;
import com.sgi.fiis.observaciones.application.usecase.ListObservationsByProcedureUseCase;
import com.sgi.fiis.observaciones.application.usecase.ListCorrectionsByObservationUseCase;
import com.sgi.fiis.observaciones.application.usecase.RegisterObservationUseCase;
import com.sgi.fiis.observaciones.application.usecase.RegisterCorrectionUseCase;
import com.sgi.fiis.observaciones.domain.exception.ObservationNotFoundException;
import com.sgi.fiis.observaciones.domain.exception.InvalidCorrectionException;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing observations and corrections on procedures.
 */
@RestController
@RequestMapping("/api/v1/observations")
@RequiredArgsConstructor
public class ObservationController {

    private final RegisterObservationUseCase registerObservationUseCase;
    private final RegisterCorrectionUseCase registerCorrectionUseCase;
    private final ListObservationsByProcedureUseCase listObservationsByProcedureUseCase;
    private final GetObservationUseCase getObservationUseCase;
    private final ListCorrectionsByObservationUseCase listCorrectionsByObservationUseCase;

    private static final String ERROR_KEY = "error";

    /**
     * Registers a new observation on a procedure.
     */
    @PostMapping
    public ResponseEntity<ObservationResponseDto> registerObservation(
            @RequestBody ObservationRequestDto dto) {
        ObservationResponseDto response = registerObservationUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Registers a correction to resolve a pending observation.
     */
    @PostMapping("/{id}/correct")
    public ResponseEntity<CorrectionResponseDto> registerCorrection(
            @PathVariable Integer id,
            @RequestBody CorrectionRequestDto dto) {
        CorrectionResponseDto response = registerCorrectionUseCase.execute(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lists all observations for a procedure.
     */
    @GetMapping("/procedure/{procedureId}")
    public ResponseEntity<List<ObservationResponseDto>> listByProcedure(
            @PathVariable Integer procedureId) {
        List<ObservationResponseDto> response = listObservationsByProcedureUseCase.execute(procedureId);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the full detail of an observation.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ObservationResponseDto> getObservation(
            @PathVariable Integer id) {
        ObservationResponseDto response = getObservationUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lists all corrections for a specific observation.
     */
    @GetMapping("/{id}/corrections")
    public ResponseEntity<List<CorrectionResponseDto>> listCorrections(
            @PathVariable Integer id) {
        List<CorrectionResponseDto> response = listCorrectionsByObservationUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ObservationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ObservationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(ERROR_KEY, ex.getMessage()));
    }

    @ExceptionHandler(InvalidCorrectionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCorrection(InvalidCorrectionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(ERROR_KEY, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(ERROR_KEY, ex.getMessage()));
    }
}
