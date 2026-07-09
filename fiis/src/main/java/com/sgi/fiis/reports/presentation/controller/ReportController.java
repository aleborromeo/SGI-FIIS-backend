package com.sgi.fiis.reports.presentation.controller;

import com.sgi.fiis.reports.application.service.ReportService;
import com.sgi.fiis.reports.domain.model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for institutional reports.
 *
 * Endpoints:
 *   GET /api/reports/projects
 *   GET /api/reports/procedures
 *   GET /api/reports/resolutions
 *   GET /api/reports/progress-reports
 *
 * All query parameters are optional and combinable.
 * Date filters expect ISO format: yyyy-MM-dd
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/projects
    // -------------------------------------------------------------------------

    /**
     * Institutional project report.
     *
     * @param groupId       (optional) filter by research group ID
     * @param status        (optional) project status: POSTULADO, OBSERVADO,
     *                      APROBADO, RECHAZADO, EN_EJECUCION, FINALIZADO
     * @param fromDate      (optional) minimum creation date (yyyy-MM-dd)
     * @param toDate        (optional) maximum creation date (yyyy-MM-dd)
     * @param researcherId  (optional) ID of the responsible investigator
     * @param callId        (optional) ID of the associated call
     * @param page          page number (default 0)
     * @param size          items per page (default 20, max 100)
     */
    @GetMapping("/projects")
    public ResponseEntity<PaginatedResponse<ProjectReport>> getProjectReport(
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) String  status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer researcherId,
            @RequestParam(required = false) Integer callId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        ReportFilter filter = new ReportFilter();
        filter.setGroupId(groupId);
        filter.setStatus(status);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setResearcherId(researcherId);
        filter.setCallId(callId);
        filter.setPage(page);
        filter.setSize(size);
        return ResponseEntity.ok(service.generateProjectReport(filter));
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/procedures
    // -------------------------------------------------------------------------

    /**
     * Institutional procedure report.
     *
     * @param groupId       (optional) filter by research group ID
     * @param status        (optional) current procedure status
     * @param fromDate      (optional) minimum submission date (yyyy-MM-dd)
     * @param toDate        (optional) maximum submission date (yyyy-MM-dd)
     * @param researcherId  (optional) ID of the procedure applicant
     * @param procedureType (optional) PROYECTO, PLAN_TESIS or INFORME_AVANCE
     */
    @GetMapping("/procedures")
    public ResponseEntity<PaginatedResponse<ProcedureReport>> getProcedureReport(
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) String  status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer researcherId,
            @RequestParam(required = false) String  procedureType,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        ReportFilter filter = new ReportFilter();
        filter.setGroupId(groupId);
        filter.setStatus(status);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setResearcherId(researcherId);
        filter.setProcedureType(procedureType);
        filter.setPage(page);
        filter.setSize(size);
        return ResponseEntity.ok(service.generateProcedureReport(filter));
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/resolutions
    // -------------------------------------------------------------------------

    /**
     * Institutional resolution report.
     *
     * @param fromDate      (optional) minimum issue date (yyyy-MM-dd)
     * @param toDate        (optional) maximum issue date (yyyy-MM-dd)
     * @param researcherId  (optional) ID of the applicant associated with the procedure
     * @param procedureType (optional) type of the procedure linked to the resolution
     */
    @GetMapping("/resolutions")
    public ResponseEntity<PaginatedResponse<ResolutionReport>> getResolutionReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Integer researcherId,
            @RequestParam(required = false) String  procedureType,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        ReportFilter filter = new ReportFilter();
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setResearcherId(researcherId);
        filter.setProcedureType(procedureType);
        filter.setPage(page);
        filter.setSize(size);
        return ResponseEntity.ok(service.generateResolutionReport(filter));
    }

    // -------------------------------------------------------------------------
    // GET /api/reports/progress-reports
    // -------------------------------------------------------------------------

    /**
     * Institutional progress report.
     *
     * @param groupId       (optional) filter by research group ID
     * @param status        (optional) report status: PENDIENTE, EN_REVISION, APROBADO, OBSERVADO, RECHAZADO
     * @param fromDate      (optional) minimum registration date (yyyy-MM-dd)
     * @param toDate        (optional) maximum registration date (yyyy-MM-dd)
     * @param procedureType (optional) report type: PARCIAL or FINAL (maps to procedureType/reportType filter)
     */
    @GetMapping("/progress-reports")
    public ResponseEntity<PaginatedResponse<ProgressReport>> getProgressReport(
            @RequestParam(required = false) Integer groupId,
            @RequestParam(required = false) String  status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String  procedureType,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        ReportFilter filter = new ReportFilter();
        filter.setGroupId(groupId);
        filter.setStatus(status);
        filter.setFromDate(fromDate);
        filter.setToDate(toDate);
        filter.setProcedureType(procedureType);
        filter.setPage(page);
        filter.setSize(size);
        return ResponseEntity.ok(service.generateProgressReport(filter));
    }
}
