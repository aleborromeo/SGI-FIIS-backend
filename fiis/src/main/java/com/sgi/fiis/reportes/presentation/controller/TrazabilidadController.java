package com.sgi.fiis.reportes.presentation.controller;

import com.sgi.fiis.reportes.application.service.TrazabilidadService;
import com.sgi.fiis.reportes.domain.model.TrazabilidadMovimiento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la trazabilidad de trámites (RF-96 a RF-99, RNF-46 a RNF-48).
 *
 * Endpoint:
 *   GET /api/reportes/trazabilidad/{idTramite}
 *
 * Devuelve el historial cronológico completo de movimientos de un trámite:
 * quién actuó, qué acción realizó, cuándo, y los cambios de estado registrados.
 */
@RestController
@RequestMapping("/api/reportes")
public class TrazabilidadController {

    private final TrazabilidadService service;

    public TrazabilidadController(TrazabilidadService service) {
        this.service = service;
    }

    /**
     * Consulta el historial de movimientos de un trámite.
     *
     * @param idTramite identificador del trámite
     * @return lista vacía si el trámite no existe o no tiene movimientos
     */
    @GetMapping("/trazabilidad/{idTramite}")
    public ResponseEntity<List<TrazabilidadMovimiento>> trazabilidad(
            @PathVariable Integer idTramite) {

        List<TrazabilidadMovimiento> historial = service.consultarTrazabilidad(idTramite);
        return ResponseEntity.ok(historial);
    }
}
