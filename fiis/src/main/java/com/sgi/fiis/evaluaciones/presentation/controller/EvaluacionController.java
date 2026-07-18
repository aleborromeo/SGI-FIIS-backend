package com.sgi.fiis.evaluaciones.presentation.controller;

import com.sgi.fiis.evaluaciones.application.dto.command.AsignarEvaluadorCommand;
import com.sgi.fiis.evaluaciones.application.dto.command.RegistrarResultadoEvaluacionCommand;
import com.sgi.fiis.evaluaciones.application.dto.response.EvaluacionResponse;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadorUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.AsignarEvaluadoresUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarDetalleAnonimoUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.ConsultarEvaluacionesUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.EvaluarEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.application.ports.in.RegistrarResultadoEvaluacionUseCase;
import com.sgi.fiis.evaluaciones.presentation.dto.AnonymousProjectDetailResponse;
import com.sgi.fiis.evaluaciones.presentation.dto.AsignarEvaluadorRequest;
import com.sgi.fiis.evaluaciones.presentation.dto.AsignarEvaluadoresRequest;
import com.sgi.fiis.evaluaciones.presentation.dto.EvaluarEvaluacionRequest;
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
@RequestMapping("/api/v1/evaluaciones")
@Tag(name = "Evaluaciones", description = "Endpoints para la gestión de evaluaciones de proyectos y documentos")
@SecurityRequirement(name = "bearerAuth")
public class EvaluacionController {

    private final AsignarEvaluadorUseCase asignarEvaluadorUseCase;
    private final AsignarEvaluadoresUseCase asignarEvaluadoresUseCase;
    private final RegistrarResultadoEvaluacionUseCase registrarResultadoEvaluacionUseCase;
    private final ConsultarEvaluacionesUseCase consultarEvaluacionesUseCase;
    private final EvaluarEvaluacionUseCase evaluarEvaluacionUseCase;
    private final ConsultarDetalleAnonimoUseCase consultarDetalleAnonimoUseCase;

    public EvaluacionController(
            AsignarEvaluadorUseCase asignarEvaluadorUseCase,
            AsignarEvaluadoresUseCase asignarEvaluadoresUseCase,
            RegistrarResultadoEvaluacionUseCase registrarResultadoEvaluacionUseCase,
            ConsultarEvaluacionesUseCase consultarEvaluacionesUseCase,
            EvaluarEvaluacionUseCase evaluarEvaluacionUseCase,
            ConsultarDetalleAnonimoUseCase consultarDetalleAnonimoUseCase
    ) {
        this.asignarEvaluadorUseCase = asignarEvaluadorUseCase;
        this.asignarEvaluadoresUseCase = asignarEvaluadoresUseCase;
        this.registrarResultadoEvaluacionUseCase = registrarResultadoEvaluacionUseCase;
        this.consultarEvaluacionesUseCase = consultarEvaluacionesUseCase;
        this.evaluarEvaluacionUseCase = evaluarEvaluacionUseCase;
        this.consultarDetalleAnonimoUseCase = consultarDetalleAnonimoUseCase;
    }

    @PostMapping("/asignar")
    @PreAuthorize("hasRole('DIRECTOR_INVESTIGACION')")
    @Operation(summary = "Asignar un evaluador", description = "Asigna un evaluador a un proyecto o plan de tesis.")
    @ApiResponse(responseCode = "201", description = "Evaluador asignado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de asignación inválidos")
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
    @PreAuthorize("hasRole('EVALUADOR')")
    @Operation(summary = "Registrar resultado de evaluación", description = "Permite a un evaluador registrar el puntaje y las observaciones de su evaluación.")
    @ApiResponse(responseCode = "200", description = "Resultado registrado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de evaluación inválidos")
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
    @PreAuthorize("hasRole('DIRECTOR_INVESTIGACION')")
    @Operation(summary = "Listar todas las evaluaciones", description = "Obtiene la lista completa de evaluaciones en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado. Requiere rol DIRECTOR_INVESTIGACION")
    public ResponseEntity<List<EvaluacionResponse>> listarTodas() {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.listarTodas());
    }

    @GetMapping("/{idEvaluacion}")
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'EVALUADOR')")
    @Operation(summary = "Obtener evaluación por ID", description = "Obtiene los detalles de una evaluación específica por su identificador.")
    @ApiResponse(responseCode = "200", description = "Evaluación encontrada")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    public ResponseEntity<EvaluacionResponse> buscarPorId(
            @PathVariable Long idEvaluacion
    ) {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.buscarPorId(idEvaluacion));
    }

    @GetMapping("/evaluador/{idEvaluador}")
    @PreAuthorize("hasAnyRole('DIRECTOR_INVESTIGACION', 'EVALUADOR')")
    @Operation(summary = "Listar evaluaciones por evaluador", description = "Obtiene la lista de evaluaciones asignadas a un evaluador en específico.")
    @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    public ResponseEntity<List<EvaluacionResponse>> listarPorEvaluador(
            @PathVariable Long idEvaluador
    ) {
        return ResponseEntity.ok(consultarEvaluacionesUseCase.listarPorEvaluador(idEvaluador));
    }

    @PostMapping("/asignar-multiple")
    @PreAuthorize("hasRole('DIRECTOR_INVESTIGACION')")
    @Operation(summary = "Asignar múltiples evaluadores", description = "Asigna varios evaluadores a un proyecto o plan de tesis de forma masiva.")
    @ApiResponse(responseCode = "201", description = "Evaluadores asignados exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de asignación inválidos")
    public ResponseEntity<List<EvaluacionResponse>> asignarEvaluadores(
            @RequestBody AsignarEvaluadoresRequest request
    ) {
        List<EvaluacionResponse> responses = asignarEvaluadoresUseCase.asignarEvaluadores(
                request.projectId(), request.planTesisId(), request.evaluadorIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PostMapping("/{idEvaluacion}/evaluar")
    @PreAuthorize("hasRole('EVALUADOR')")
    @Operation(summary = "Evaluar expediente", description = "Permite a un evaluador enviar el formulario completo de evaluación con criterios y dictamen.")
    @ApiResponse(responseCode = "200", description = "Evaluación registrada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de evaluación inválidos")
    public ResponseEntity<EvaluacionResponse> evaluar(
            @PathVariable Long idEvaluacion,
            @RequestBody EvaluarEvaluacionRequest request
    ) {
        EvaluacionResponse response = evaluarEvaluacionUseCase.evaluar(idEvaluacion, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{idEvaluacion}/detalle-anonimo")
    @PreAuthorize("hasAnyRole('EVALUADOR', 'DIRECTOR_INVESTIGACION')")
    @Operation(summary = "Obtener detalle anónimo", description = "Obtiene el detalle anónimo de un expediente para la evaluación bajo modalidad de par ciego.")
    @ApiResponse(responseCode = "200", description = "Detalle anónimo recuperado")
    @ApiResponse(responseCode = "403", description = "Acceso denegado")
    public ResponseEntity<AnonymousProjectDetailResponse> consultarDetalleAnonimo(
            @PathVariable Long idEvaluacion
    ) {
        AnonymousProjectDetailResponse response = consultarDetalleAnonimoUseCase.consultarDetalleAnonimo(idEvaluacion);
        return ResponseEntity.ok(response);
    }
}