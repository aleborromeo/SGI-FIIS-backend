package com.sgi.fiis.evaluaciones.presentation.controller;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadorUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarEvaluacionesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.RegistrarResultadoEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.presentation.dto.AsignarEvaluadorRequest;
import com.sgi.fiis.evaluaciones.presentation.dto.RegistrarResultadoEvaluacionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluaciones")
public class EvaluacionController {

    private final AsignarEvaluadorUseCase asignarEvaluadorUseCase;
    private final RegistrarResultadoEvaluacionUseCase registrarResultadoEvaluacionUseCase;
    private final ConsultarEvaluacionesUseCase consultarEvaluacionesUseCase;

    public EvaluacionController(
            AsignarEvaluadorUseCase asignarEvaluadorUseCase,
            RegistrarResultadoEvaluacionUseCase registrarResultadoEvaluacionUseCase,
            ConsultarEvaluacionesUseCase consultarEvaluacionesUseCase
    ) {
        this.asignarEvaluadorUseCase = asignarEvaluadorUseCase;
        this.registrarResultadoEvaluacionUseCase = registrarResultadoEvaluacionUseCase;
        this.consultarEvaluacionesUseCase = consultarEvaluacionesUseCase;
    }

    @PostMapping("/asignar")
    public ResponseEntity<EvaluacionResponse> asignarEvaluador(
            @RequestBody AsignarEvaluadorRequest request
    ) {
        AsignarEvaluadorCommand command = new AsignarEvaluadorCommand(
                request.idProyecto(),
                request.idPlanTesis(),
                request.idEvaluador()
        );

        EvaluacionResponse response = asignarEvaluadorUseCase.asignarEvaluador(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{idEvaluacion}/resultado")
    public ResponseEntity<EvaluacionResponse> registrarResultado(
            @PathVariable Long idEvaluacion,
            @RequestBody RegistrarResultadoEvaluacionRequest request
    ) {
        RegistrarResultadoEvaluacionCommand command = new RegistrarResultadoEvaluacionCommand(
                idEvaluacion,
                request.idEvaluador(),
                request.resultado(),
                request.puntaje(),
                request.observaciones()
        );

        EvaluacionResponse response = registrarResultadoEvaluacionUseCase.registrarResultado(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EvaluacionResponse>> listarTodas() {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.listarTodas());
    }

    @GetMapping("/{idEvaluacion}")
    public ResponseEntity<EvaluacionResponse> buscarPorId(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.buscarPorId(idEvaluacion));
    }

    @GetMapping("/evaluador/{idEvaluador}")
    public ResponseEntity<List<EvaluacionResponse>> listarPorEvaluador(
            @PathVariable Long idEvaluador
    ) {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.listarPorEvaluador(idEvaluador));
    }
}