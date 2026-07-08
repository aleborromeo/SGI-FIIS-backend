package com.sgi.fiis.thesis.presentation.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.port.in.ThesisReportUseCase;

@RestController
@RequestMapping("/api/v1/thesis/reports")
public class ThesisReportController {
    private final ThesisReportUseCase thesisReportUseCase;
    public ThesisReportController(ThesisReportUseCase thesisReportUseCase) { this.thesisReportUseCase = thesisReportUseCase; }

    @PostMapping
    public ResponseEntity<ThesisReportResponse> registrar(@Valid @RequestBody RegisterThesisReportCommand command) {
        ThesisReportResponse response = thesisReportUseCase.registrarInformeFinal(command);
        return ResponseEntity.created(URI.create("/api/v1/thesis/reports/" + response.idInformeTesis())).body(response);
    }

    @GetMapping("/{idInformeTesis}")
    public ResponseEntity<ThesisReportResponse> obtener(@PathVariable Integer idInformeTesis) {
        return ResponseEntity.ok(thesisReportUseCase.obtenerPorId(idInformeTesis));
    }

    @GetMapping("/plan/{idPlanTesis}")
    public ResponseEntity<List<ThesisReportResponse>> listarPorPlan(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(thesisReportUseCase.listarPorPlan(idPlanTesis));
    }

    @PatchMapping("/{idInformeTesis}/approve")
    public ResponseEntity<ThesisReportResponse> aprobar(@PathVariable Integer idInformeTesis) {
        return ResponseEntity.ok(thesisReportUseCase.aprobarInforme(idInformeTesis));
    }

    @PatchMapping("/{idInformeTesis}/observe")
    public ResponseEntity<ThesisReportResponse> observar(@PathVariable Integer idInformeTesis, @RequestParam String observacion) {
        return ResponseEntity.ok(thesisReportUseCase.observarInforme(idInformeTesis, observacion));
    }
}
