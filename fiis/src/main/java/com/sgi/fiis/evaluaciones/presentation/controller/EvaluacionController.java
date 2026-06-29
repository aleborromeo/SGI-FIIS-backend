package com.sgi.fiis.evaluaciones.presentation.controller;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadorUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarEvaluacionesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.RegistrarResultadoEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.presentation.dto.AsignarEvaluadorRequest;
import com.sgi.fiis.evaluaciones.presentation.dto.RegistrarResultadoEvaluacionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluaciones")
@Tag(name = "Evaluaciones", description = "Endpoints para la gestiÃ³n de evaluaciones de proyectos y documentos")
@SecurityRequirement(name = "bearerAuth")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION')")
    @Operation(summary = "Asignar un evaluador", description = "Asigna un evaluador a un proyecto o plan de tesis.")
    @ApiResponse(responseCode = "201", description = "Evaluador asignado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de asignaciÃ³n invÃ¡lidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado. Requiere rol DIRECTOR_INVESTIGACION")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'EVALUADOR')")
    @Operation(summary = "Registrar resultado de evaluaciÃ³n", description = "Permite a un evaluador registrar el puntaje y las observaciones de su evaluaciÃ³n.")
    @ApiResponse(responseCode = "200", description = "Resultado registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de evaluaciÃ³n invÃ¡lidos")
    @ApiResponse(responseCode = "403", description = "Acceso denegado. Requiere rol EVALUADOR")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION')")
    @Operation(summary = "Listar todas las evaluaciones", description = "Obtiene la lista completa de evaluaciones en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado. Requiere rol DIRECTOR_INVESTIGACION")
    public ResponseEntity<List<EvaluacionResponse>> listarTodas() {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.listarTodas());
    }

    @GetMapping("/{idEvaluacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION', 'EVALUADOR')")
    @Operation(summary = "Obtener evaluaciÃ³n por ID", description = "Obtiene los detalles de una evaluaciÃ³n especÃ­fica por su identificador.")
    @ApiResponse(responseCode = "200", description = "EvaluaciÃ³n encontrada")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @ApiResponse(responseCode = "404", description = "EvaluaciÃ³n no encontrada")
    public ResponseEntity<EvaluacionResponse> buscarPorId(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.buscarPorId(idEvaluacion));
    }

    @GetMapping("/evaluador/{idEvaluador}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR_INVESTIGACION', 'EVALUADOR')")
    @Operation(summary = "Listar evaluaciones por evaluador", description = "Obtiene la lista de evaluaciones asignadas a un evaluador en especÃ­fico.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    public ResponseEntity<List<EvaluacionResponse>> listarPorEvaluador(
            @PathVariable Long idEvaluador
    ) {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.listarPorEvaluador(idEvaluador));
    }
}