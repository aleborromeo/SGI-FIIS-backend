package com.sgi.fiis.thesis.presentation.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.port.in.ThesisReportUseCase;

@RestController
@RequestMapping("/api/v1/thesis/reports")
public class ThesisReportController {
    private final ThesisReportUseCase thesisReportUseCase;
    public ThesisReportController(ThesisReportUseCase thesisReportUseCase) { this.thesisReportUseCase = thesisReportUseCase; }

    @PostMapping
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<ThesisReportResponse> registrar(@Valid @RequestBody RegisterThesisReportCommand command) {
        ThesisReportResponse response = thesisReportUseCase.registrarInformeFinal(command);
        return ResponseEntity.created(URI.create("/api/v1/thesis/reports/" + response.idInformeTesis())).body(response);
    }

    @GetMapping("/{idInformeTesis}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION', 'DECANO', 'ADMIN')")
    public ResponseEntity<ThesisReportResponse> obtener(@PathVariable Integer idInformeTesis) {
        return ResponseEntity.ok(thesisReportUseCase.obtenerPorId(idInformeTesis));
    }

    @GetMapping("/plan/{idPlanTesis}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION', 'DECANO', 'ADMIN')")
    public ResponseEntity<List<ThesisReportResponse>> listarPorPlan(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(thesisReportUseCase.listarPorPlan(idPlanTesis));
    }

    @PatchMapping("/{idInformeTesis}/approve")
    @PreAuthorize("hasAnyRole('COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION')")
    public ResponseEntity<ThesisReportResponse> aprobar(@PathVariable Integer idInformeTesis) {
        return ResponseEntity.ok(thesisReportUseCase.aprobarInforme(idInformeTesis));
    }

    @PatchMapping("/{idInformeTesis}/observe")
    @PreAuthorize("hasAnyRole('COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION')")
    public ResponseEntity<ThesisReportResponse> observar(@PathVariable Integer idInformeTesis, @RequestParam String observacion) {
        return ResponseEntity.ok(thesisReportUseCase.observarInforme(idInformeTesis, observacion));
    }
}
