package com.sgi.fiis.thesis.presentation.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.sgi.fiis.thesis.application.dto.*;
import com.sgi.fiis.thesis.domain.ReviewerRole;
import com.sgi.fiis.thesis.domain.port.in.ThesisPlanUseCase;

@RestController
@RequestMapping("/api/v1/thesis/plans")
public class ThesisPlanController {
    private final ThesisPlanUseCase thesisPlanUseCase;
    public ThesisPlanController(ThesisPlanUseCase thesisPlanUseCase) { this.thesisPlanUseCase = thesisPlanUseCase; }

    @PostMapping
    public ResponseEntity<ThesisPlanResponse> registrar(@Valid @RequestBody RegisterThesisPlanCommand command) {
        ThesisPlanResponse response = thesisPlanUseCase.registrarPlan(command);
        return ResponseEntity.created(URI.create("/api/v1/thesis/plans/" + response.idPlanTesis())).body(response);
    }

    @GetMapping("/{idPlanTesis}")
    public ResponseEntity<ThesisPlanResponse> obtener(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(thesisPlanUseCase.obtenerPorId(idPlanTesis));
    }

    @GetMapping("/student/{idEstudiante}")
    public ResponseEntity<List<ThesisPlanResponse>> listarPorEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(thesisPlanUseCase.listarPorEstudiante(idEstudiante));
    }

    @GetMapping("/group/{idGrupo}")
    public ResponseEntity<List<ThesisPlanResponse>> listarPorGrupo(@PathVariable Integer idGrupo) {
        return ResponseEntity.ok(thesisPlanUseCase.listarPorGrupo(idGrupo));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ThesisPlanResponse>> listarPendientes(@RequestParam ReviewerRole revisor) {
        return ResponseEntity.ok(thesisPlanUseCase.listarPendientesPorRevisor(revisor));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/approve")
    public ResponseEntity<ThesisPlanResponse> aprobarCoordinador(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(thesisPlanUseCase.aprobarPorCoordinador(idPlanTesis));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/observe")
    public ResponseEntity<ThesisPlanResponse> observarCoordinador(@PathVariable Integer idPlanTesis, @Valid @RequestBody ObserveThesisPlanCommand command) {
        return ResponseEntity.ok(thesisPlanUseCase.observarPorCoordinador(idPlanTesis, command));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/reject")
    public ResponseEntity<ThesisPlanResponse> rechazarCoordinador(@PathVariable Integer idPlanTesis, @RequestParam String motivo) {
        return ResponseEntity.ok(thesisPlanUseCase.rechazarPorCoordinador(idPlanTesis, motivo));
    }

    @PatchMapping("/{idPlanTesis}/director/approve")
    public ResponseEntity<ThesisPlanResponse> aprobarDirector(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(thesisPlanUseCase.aprobarPorDirector(idPlanTesis));
    }

    @PatchMapping("/{idPlanTesis}/director/observe")
    public ResponseEntity<ThesisPlanResponse> observarDirector(@PathVariable Integer idPlanTesis, @Valid @RequestBody ObserveThesisPlanCommand command) {
        return ResponseEntity.ok(thesisPlanUseCase.observarPorDirector(idPlanTesis, command));
    }

    @PatchMapping("/{idPlanTesis}/rectify")
    public ResponseEntity<ThesisPlanResponse> rectify(@PathVariable Integer idPlanTesis, @Valid @RequestBody RectifyThesisPlanCommand command) {
        return ResponseEntity.ok(thesisPlanUseCase.subsanarPlan(idPlanTesis, command));
    }

    @PostMapping("/{idPlanTesis}/dean/resolution")
    public ResponseEntity<ThesisPlanResponse> registrarResolucion(@PathVariable Integer idPlanTesis,
            @Valid @RequestBody RegisterResolutionCommand command) {
        return ResponseEntity.ok(thesisPlanUseCase.registrarResolucion(idPlanTesis, command));
    }
}
