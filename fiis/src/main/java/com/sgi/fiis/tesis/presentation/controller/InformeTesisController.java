package pe.unas.fiis.sgifiis.thesis.presentation.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import pe.unas.fiis.sgifiis.thesis.application.dto.*;
import pe.unas.fiis.sgifiis.thesis.domain.port.in.InformeTesisUseCase;

@RestController
@RequestMapping("/api/v1/thesis/reports")
public class InformeTesisController {
    private final InformeTesisUseCase informeTesisUseCase;
    public InformeTesisController(InformeTesisUseCase informeTesisUseCase) { this.informeTesisUseCase = informeTesisUseCase; }

    @PostMapping
    public ResponseEntity<InformeTesisResponse> registrar(@Valid @RequestBody RegistrarInformeTesisCommand command) {
        InformeTesisResponse response = informeTesisUseCase.registrarInformeFinal(command);
        return ResponseEntity.created(URI.create("/api/v1/thesis/reports/" + response.idInformeTesis())).body(response);
    }

    @GetMapping("/{idInformeTesis}")
    public ResponseEntity<InformeTesisResponse> obtener(@PathVariable Integer idInformeTesis) {
        return ResponseEntity.ok(informeTesisUseCase.obtenerPorId(idInformeTesis));
    }

    @GetMapping("/plan/{idPlanTesis}")
    public ResponseEntity<List<InformeTesisResponse>> listarPorPlan(@PathVariable Integer idPlanTesis) {
        return ResponseEntity.ok(informeTesisUseCase.listarPorPlan(idPlanTesis));
    }

    @PatchMapping("/{idInformeTesis}/approve")
    public ResponseEntity<InformeTesisResponse> aprobar(@PathVariable Integer idInformeTesis, @RequestParam Integer idUsuarioAccion) {
        return ResponseEntity.ok(informeTesisUseCase.aprobarInforme(idInformeTesis, idUsuarioAccion));
    }

    @PatchMapping("/{idInformeTesis}/observe")
    public ResponseEntity<InformeTesisResponse> observar(@PathVariable Integer idInformeTesis, @RequestParam Integer idUsuarioAccion, @RequestParam String observacion) {
        return ResponseEntity.ok(informeTesisUseCase.observarInforme(idInformeTesis, idUsuarioAccion, observacion));
    }
}
