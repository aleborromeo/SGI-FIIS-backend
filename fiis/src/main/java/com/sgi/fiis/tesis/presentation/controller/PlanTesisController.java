package pe.unas.fiis.sgifiis.thesis.presentation.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import pe.unas.fiis.sgifiis.thesis.application.dto.*;
import pe.unas.fiis.sgifiis.thesis.domain.RolRevisor;
import pe.unas.fiis.sgifiis.thesis.domain.port.in.PlanTesisUseCase;

@RestController
@RequestMapping("/api/v1/thesis/plans")
public class PlanTesisController {
    private final PlanTesisUseCase planTesisUseCase;
    public PlanTesisController(PlanTesisUseCase planTesisUseCase) { this.planTesisUseCase = planTesisUseCase; }

    @PostMapping
    public ResponseEntity<PlanTesisResponse> registrar(@Valid @RequestBody RegistrarPlanTesisCommand command) {
        PlanTesisResponse response = planTesisUseCase.registrarPlan(command);
        return ResponseEntity.created(URI.create("/api/v1/thesis/plans/" + response.idPlanTesis())).body(response);
    }

    @GetMapping("/{idPlanTesis}")
    public ResponseEntity<PlanTesisResponse> obtener(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(planTesisUseCase.obtenerPorId(idPlanTesis));
    }

    @GetMapping("/student/{idEstudiante}")
    public ResponseEntity<List<PlanTesisResponse>> listarPorEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(planTesisUseCase.listarPorEstudiante(idEstudiante));
    }

    @GetMapping("/group/{idGrupo}")
    public ResponseEntity<List<PlanTesisResponse>> listarPorGrupo(@PathVariable Integer idGrupo) {
        return ResponseEntity.ok(planTesisUseCase.listarPorGrupo(idGrupo));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<PlanTesisResponse>> listarPendientes(@RequestParam RolRevisor revisor) {
        return ResponseEntity.ok(planTesisUseCase.listarPendientesPorRevisor(revisor));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/approve")
    public ResponseEntity<PlanTesisResponse> aprobarCoordinador(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(planTesisUseCase.aprobarPorCoordinador(idPlanTesis));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/observe")
    public ResponseEntity<PlanTesisResponse> observarCoordinador(@PathVariable Integer idPlanTesis, @Valid @RequestBody ObservarPlanTesisCommand command) {
        return ResponseEntity.ok(planTesisUseCase.observarPorCoordinador(idPlanTesis, command));
    }

    @PatchMapping("/{idPlanTesis}/coordinator/reject")
    public ResponseEntity<PlanTesisResponse> rechazarCoordinador(@PathVariable Integer idPlanTesis, @RequestParam String motivo) {
        return ResponseEntity.ok(planTesisUseCase.rechazarPorCoordinador(idPlanTesis, motivo));
    }

    @PatchMapping("/{idPlanTesis}/director/approve")
    public ResponseEntity<PlanTesisResponse> aprobarDirector(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(planTesisUseCase.aprobarPorDirector(idPlanTesis));
    }

    @PatchMapping("/{idPlanTesis}/director/observe")
    public ResponseEntity<PlanTesisResponse> observarDirector(@PathVariable Integer idPlanTesis, @Valid @RequestBody ObservarPlanTesisCommand command) {
        return ResponseEntity.ok(planTesisUseCase.observarPorDirector(idPlanTesis, command));
    }

    @PatchMapping("/{idPlanTesis}/subsanar")
    public ResponseEntity<PlanTesisResponse> subsanar(@PathVariable Integer idPlanTesis, @Valid @RequestBody SubsanarPlanTesisCommand command) {
        return ResponseEntity.ok(planTesisUseCase.subsanarPlan(idPlanTesis, command));
    }

    @PostMapping("/{idPlanTesis}/dean/resolution")
    public ResponseEntity<PlanTesisResponse> registrarResolucion(@PathVariable Integer idPlanTesis,
            @Valid @RequestBody RegistrarResolucionCommand command) {
        return ResponseEntity.ok(planTesisUseCase.registrarResolucion(idPlanTesis, command));
    }
}
