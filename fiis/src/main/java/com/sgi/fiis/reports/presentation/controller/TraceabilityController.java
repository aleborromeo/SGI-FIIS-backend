package com.sgi.fiis.reports.presentation.controller;

import com.sgi.fiis.reports.application.service.TraceabilityService;
import com.sgi.fiis.reports.domain.model.TraceabilityMovement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for procedure traceability.
 *
 * Endpoint:
 *   GET /api/reports/traceability/{procedureId}
 *
 * Returns the complete chronological history of movements for a procedure:
 * who acted, what action was performed, when, and the status changes recorded.
 */
@RestController
@RequestMapping("/api/reports")
public class TraceabilityController {

    private final TraceabilityService service;

    public TraceabilityController(TraceabilityService service) {
        this.service = service;
    }

    /**
     * Queries the history of movements for a procedure.
     *
     * @param procedureId identifier of the procedure
     * @return empty list if the procedure does not exist or has no movements
     */
    @GetMapping("/traceability/{procedureId}")
    public ResponseEntity<List<TraceabilityMovement>> getTraceability(
            @PathVariable Integer procedureId) {

        List<TraceabilityMovement> history = service.getTraceability(procedureId);
        return ResponseEntity.ok(history);
    }
}
