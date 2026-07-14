package com.sgi.fiis.thesis.presentation.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<ThesisPlanResponse> registrar(@Valid @RequestBody RegisterThesisPlanCommand command) {
        ThesisPlanResponse response = thesisPlanUseCase.registrarPlan(command);
        return ResponseEntity.created(URI.create("/api/v1/thesis/plans/" + response.idPlanTesis())).body(response);
    }

    @GetMapping("/{idPlanTesis}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION', 'DECANO', 'ADMIN')")
    public ResponseEntity<ThesisPlanResponse> obtener(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(thesisPlanUseCase.obtenerPorId(idPlanTesis));
    }

    @GetMapping("/student/{idEstudiante}")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION', 'DECANO', 'ADMIN')")
    public ResponseEntity<List<ThesisPlanResponse>> listarPorEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(thesisPlanUseCase.listarPorEstudiante(idEstudiante));
    }

    @GetMapping("/group/{idGrupo}")
    @PreAuthorize("hasAnyRole('COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION', 'ADMIN')")
    public ResponseEntity<List<ThesisPlanResponse>> listarPorGrupo(@PathVariable Integer idGrupo) {
        return ResponseEntity.ok(thesisPlanUseCase.listarPorGrupo(idGrupo));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('COORDINADOR_GRUPO', 'DIRECTOR_INVESTIGACION', 'DECANO', 'ADMIN')")
    public ResponseEntity<List<ThesisPlanResponse>> listarPendientes(@RequestParam ReviewerRole revisor) {
        return ResponseEntity.ok(thesisPlanUseCase.listarPendientesPorRevisor(revisor));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/approve")
    @PreAuthorize("hasRole('COORDINADOR_GRUPO')")
    public ResponseEntity<ThesisPlanResponse> aprobarCoordinador(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(thesisPlanUseCase.aprobarPorCoordinador(idPlanTesis));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/observe")
    @PreAuthorize("hasRole('COORDINADOR_GRUPO')")
    public ResponseEntity<ThesisPlanResponse> observarCoordinador(@PathVariable Integer idPlanTesis, @Valid @RequestBody ObserveThesisPlanCommand command) {
        return ResponseEntity.ok(thesisPlanUseCase.observarPorCoordinador(idPlanTesis, command));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/reject")
    @PreAuthorize("hasRole('COORDINADOR_GRUPO')")
    public ResponseEntity<ThesisPlanResponse> rechazarCoordinador(@PathVariable Integer idPlanTesis, @RequestParam String motivo) {
        return ResponseEntity.ok(thesisPlanUseCase.rechazarPorCoordinador(idPlanTesis, motivo));
    }

    @PatchMapping("/{idPlanTesis}/director/approve")
    @PreAuthorize("hasRole('DIRECTOR_INVESTIGACION')")
    public ResponseEntity<ThesisPlanResponse> aprobarDirector(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(thesisPlanUseCase.aprobarPorDirector(idPlanTesis));
    }

    @PatchMapping("/{idPlanTesis}/director/observe")
    @PreAuthorize("hasRole('DIRECTOR_INVESTIGACION')")
    public ResponseEntity<ThesisPlanResponse> observarDirector(@PathVariable Integer idPlanTesis, @Valid @RequestBody ObserveThesisPlanCommand command) {
        return ResponseEntity.ok(thesisPlanUseCase.observarPorDirector(idPlanTesis, command));
    }

    @PatchMapping("/{idPlanTesis}/rectify")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<ThesisPlanResponse> rectify(@PathVariable Integer idPlanTesis, @Valid @RequestBody RectifyThesisPlanCommand command) {
        return ResponseEntity.ok(thesisPlanUseCase.subsanarPlan(idPlanTesis, command));
    }

    @PostMapping("/{idPlanTesis}/dean/resolution")
    @PreAuthorize("hasRole('DECANO')")
    public ResponseEntity<ThesisPlanResponse> registrarResolucion(@PathVariable Integer idPlanTesis,
            @Valid @RequestBody RegisterResolutionCommand command) {
        return ResponseEntity.ok(thesisPlanUseCase.registrarResolucion(idPlanTesis, command));
    }
}
