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

import com.sgi.fiis.observaciones.application.dto.ObservacionRequestDTO;
import com.sgi.fiis.observaciones.application.dto.ObservacionResponseDTO;
import com.sgi.fiis.observaciones.application.dto.SubsanacionRequestDTO;
import com.sgi.fiis.observaciones.application.dto.SubsanacionResponseDTO;
import com.sgi.fiis.observaciones.application.usecase.ConsultaObservacionUseCase;
import com.sgi.fiis.observaciones.application.usecase.ListaObservacionesPorTramiteUseCase;
import com.sgi.fiis.observaciones.application.usecase.ListaSubsanacionesPorObservacionUseCase;
import com.sgi.fiis.observaciones.application.usecase.RegistraObservacionUseCase;
import com.sgi.fiis.observaciones.application.usecase.RegistraSubsanacionUseCase;
import com.sgi.fiis.observaciones.domain.exception.ObservacionNotFoundException;
import com.sgi.fiis.observaciones.domain.exception.SubsanacionInvalidaException;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para gestionar observaciones y subsanaciones de trámites.
 */
@RestController
@RequestMapping("/api/observaciones")
@RequiredArgsConstructor
public class ObservacionController {

    private final RegistraObservacionUseCase registraObservacionUseCase;
    private final RegistraSubsanacionUseCase registraSubsanacionUseCase;
    private final ListaObservacionesPorTramiteUseCase listaObservacionesPorTramiteUseCase;
    private final ConsultaObservacionUseCase consultaObservacionUseCase;
    private final ListaSubsanacionesPorObservacionUseCase listaSubsanacionesPorObservacionUseCase;

    /**
     * Registra una nueva observación sobre un trámite.
     */
    @PostMapping
    public ResponseEntity<ObservacionResponseDTO> registrarObservacion(
            @RequestBody ObservacionRequestDTO dto) {
        ObservacionResponseDTO response = registraObservacionUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Registra una subsanación para levantar una observación pendiente.
     */
    @PostMapping("/{id}/subsanar")
    public ResponseEntity<SubsanacionResponseDTO> registrarSubsanacion(
            @PathVariable Integer id,
            @RequestBody SubsanacionRequestDTO dto) {
        SubsanacionResponseDTO response = registraSubsanacionUseCase.execute(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas las observaciones de un trámite.
     */
    @GetMapping("/tramite/{tramiteId}")
    public ResponseEntity<List<ObservacionResponseDTO>> listarPorTramite(
            @PathVariable Integer tramiteId) {
        List<ObservacionResponseDTO> response = listaObservacionesPorTramiteUseCase.execute(tramiteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta el detalle completo de una observación.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ObservacionResponseDTO> consultarObservacion(
            @PathVariable Integer id) {
        ObservacionResponseDTO response = consultaObservacionUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todas las subsanaciones de una observación específica.
     */
    @GetMapping("/{id}/subsanaciones")
    public ResponseEntity<List<SubsanacionResponseDTO>> listarSubsanaciones(
            @PathVariable Integer id) {
        List<SubsanacionResponseDTO> response = listaSubsanacionesPorObservacionUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(ObservacionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ObservacionNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(SubsanacionInvalidaException.class)
    public ResponseEntity<Map<String, String>> handleInvalidSubsanacion(SubsanacionInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
}
