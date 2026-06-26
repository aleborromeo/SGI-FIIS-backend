package com.sgi.fiis.tramites.presentation.controller;

import com.sgi.fiis.auth.infrastructure.security.CustomUserDetails;
import com.sgi.fiis.shared.domain.exception.BusinessException;
import com.sgi.fiis.tramites.application.dto.*;
import com.sgi.fiis.tramites.application.usecase.*;
import com.sgi.fiis.users.domain.model.RoleEnum;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tramites")
public class TramiteController {

    private final CrearTramiteUseCase crearTramiteUseCase;
    private final AprobarTramiteUseCase aprobarTramiteUseCase;
    private final ObservarTramiteUseCase observarTramiteUseCase;
    private final SubsanarTramiteUseCase subsanarTramiteUseCase;
    private final RechazarTramiteUseCase rechazarTramiteUseCase;
    private final RegistrarResolucionUseCase registrarResolucionUseCase;
    private final ConsultarTrazabilidadUseCase consultarTrazabilidadUseCase;

    public TramiteController(CrearTramiteUseCase crearTramiteUseCase,
                             AprobarTramiteUseCase aprobarTramiteUseCase,
                             ObservarTramiteUseCase observarTramiteUseCase,
                             SubsanarTramiteUseCase subsanarTramiteUseCase,
                             RechazarTramiteUseCase rechazarTramiteUseCase,
                             RegistrarResolucionUseCase registrarResolucionUseCase,
                             ConsultarTrazabilidadUseCase consultarTrazabilidadUseCase) {
        this.crearTramiteUseCase           = crearTramiteUseCase;
        this.aprobarTramiteUseCase         = aprobarTramiteUseCase;
        this.observarTramiteUseCase        = observarTramiteUseCase;
        this.subsanarTramiteUseCase        = subsanarTramiteUseCase;
        this.rechazarTramiteUseCase        = rechazarTramiteUseCase;
        this.registrarResolucionUseCase    = registrarResolucionUseCase;
        this.consultarTrazabilidadUseCase  = consultarTrazabilidadUseCase;
    }

    @PostMapping
    public ResponseEntity<TramiteResponseDto> crear(
            @Valid @RequestBody TramiteRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        dto.setIdSolicitante(userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crearTramiteUseCase.execute(dto));
    }

    @PutMapping("/{id}/aprobar")
    public ResponseEntity<TramiteResponseDto> aprobar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                aprobarTramiteUseCase.execute(id, extraerRol(userDetails), userDetails.getId()));
    }

    @PutMapping("/{id}/observar")
    public ResponseEntity<TramiteResponseDto> observar(
            @PathVariable Long id,
            @Valid @RequestBody ObservarTramiteRequestDto body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                observarTramiteUseCase.execute(
                        id, extraerRol(userDetails), userDetails.getId(), body.getTextoObservacion()));
    }

    @PutMapping("/{id}/subsanar")
    public ResponseEntity<TramiteResponseDto> subsanar(
            @PathVariable Long id,
            @Valid @RequestBody SubsanarTramiteRequestDto body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                subsanarTramiteUseCase.execute(id, userDetails.getId(), body.getDetalleSubsanacion()));
    }

    @PutMapping("/{id}/rechazar")
    public ResponseEntity<TramiteResponseDto> rechazar(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                rechazarTramiteUseCase.execute(id, extraerRol(userDetails), userDetails.getId()));
    }

    @PutMapping("/{id}/resolucion")
    public ResponseEntity<TramiteResponseDto> registrarResolucion(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(
                registrarResolucionUseCase.execute(id, userDetails.getId()));
    }

    @GetMapping("/{id}/trazabilidad")
    public ResponseEntity<List<MovimientoResponseDto>> consultarTrazabilidad(@PathVariable Long id) {
        return ResponseEntity.ok(consultarTrazabilidadUseCase.execute(id));
    }

    private RoleEnum extraerRol(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> RoleEnum.valueOf(a.getAuthority().replace("ROLE_", "")))
                .orElseThrow(() -> new BusinessException("El usuario autenticado no tiene un rol asignado"));
    }
}
