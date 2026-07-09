package com.sgi.fiis.reportes.presentation.controller;

import com.sgi.fiis.reportes.application.service.ReporteService;
import com.sgi.fiis.reportes.domain.model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controlador REST para los reportes institucionales (RF-94, RF-95).
 *
 * Endpoints disponibles:
 *   GET /api/reportes/proyectos
 *   GET /api/reportes/tramites
 *   GET /api/reportes/resoluciones
 *   GET /api/reportes/informes-avance
 *
 * Todos los parámetros de query son opcionales y combinables.
 * Los filtros de fecha esperan el formato ISO: yyyy-MM-dd
 */
@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasAnyRole('ADMIN', 'DECANO', 'DIRECTOR_INVESTIGACION', 'COORDINADOR_GRUPO')")
public class ReporteController {

    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    // -------------------------------------------------------------------------
    // GET /api/reportes/proyectos
    // -------------------------------------------------------------------------

    /**
     * Reporte institucional de proyectos de investigación.
     *
     * @param idGrupo        (opcional) filtrar por id del grupo de investigación
     * @param estado         (opcional) estado del proyecto: POSTULADO, OBSERVADO,
     *                       APROBADO, RECHAZADO, EN_EJECUCION, FINALIZADO
     * @param fechaDesde     (opcional) fecha mínima de creación (yyyy-MM-dd)
     * @param fechaHasta     (opcional) fecha máxima de creación (yyyy-MM-dd)
     * @param idInvestigador (opcional) id del responsable del proyecto
     * @param idConvocatoria (opcional) id de la convocatoria asociada
     * @param page           número de página (default 0)
     * @param size           elementos por página (default 20, máximo 100)
     */
    @GetMapping("/proyectos")
    public ResponseEntity<PaginatedResponse<ReporteProyecto>> reporteProyectos(
            @RequestParam(required = false) Integer idGrupo,
            @RequestParam(required = false) String  estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Integer idInvestigador,
            @RequestParam(required = false) Integer idConvocatoria,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(idGrupo);
        filtro.setEstado(estado);
        filtro.setFechaDesde(fechaDesde);
        filtro.setFechaHasta(fechaHasta);
        filtro.setIdInvestigador(idInvestigador);
        filtro.setIdConvocatoria(idConvocatoria);
        filtro.setPage(page);
        filtro.setSize(size);
        return ResponseEntity.ok(service.generarReporteProyectos(filtro));
    }

    // -------------------------------------------------------------------------
    // GET /api/reportes/tramites
    // -------------------------------------------------------------------------

    /**
     * Reporte institucional de trámites.
     *
     * @param idGrupo        (opcional) filtrar por grupo de investigación
     * @param estado         (opcional) estado actual del trámite
     * @param fechaDesde     (opcional) fecha mínima de envío (yyyy-MM-dd)
     * @param fechaHasta     (opcional) fecha máxima de envío (yyyy-MM-dd)
     * @param idInvestigador (opcional) id del solicitante del trámite
     * @param tipoTramite    (opcional) PROYECTO, PLAN_TESIS o INFORME_AVANCE
     */
    @GetMapping("/tramites")
    public ResponseEntity<PaginatedResponse<ReporteTramite>> reporteTramites(
            @RequestParam(required = false) Integer idGrupo,
            @RequestParam(required = false) String  estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Integer idInvestigador,
            @RequestParam(required = false) String  tipoTramite,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(idGrupo);
        filtro.setEstado(estado);
        filtro.setFechaDesde(fechaDesde);
        filtro.setFechaHasta(fechaHasta);
        filtro.setIdInvestigador(idInvestigador);
        filtro.setTipoTramite(tipoTramite);
        filtro.setPage(page);
        filtro.setSize(size);
        return ResponseEntity.ok(service.generarReporteTramites(filtro));
    }

    // -------------------------------------------------------------------------
    // GET /api/reportes/resoluciones
    // -------------------------------------------------------------------------

    /**
     * Reporte institucional de resoluciones emitidas.
     *
     * @param fechaDesde     (opcional) fecha mínima de emisión (yyyy-MM-dd)
     * @param fechaHasta     (opcional) fecha máxima de emisión (yyyy-MM-dd)
     * @param idInvestigador (opcional) id del solicitante asociado al trámite
     * @param tipoTramite    (opcional) tipo del trámite vinculado a la resolución
     */
    @GetMapping("/resoluciones")
    public ResponseEntity<PaginatedResponse<ReporteResolucion>> reporteResoluciones(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) Integer idInvestigador,
            @RequestParam(required = false) String  tipoTramite,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        FiltroReporte filtro = new FiltroReporte();
        filtro.setFechaDesde(fechaDesde);
        filtro.setFechaHasta(fechaHasta);
        filtro.setIdInvestigador(idInvestigador);
        filtro.setTipoTramite(tipoTramite);
        filtro.setPage(page);
        filtro.setSize(size);
        return ResponseEntity.ok(service.generarReporteResoluciones(filtro));
    }

    // -------------------------------------------------------------------------
    // GET /api/reportes/informes-avance
    // -------------------------------------------------------------------------

    /**
     * Reporte institucional de informes de avance de proyectos.
     *
     * @param idGrupo    (opcional) filtrar por grupo de investigación
     * @param estado     (opcional) estado del informe: PENDIENTE, EN_REVISION,
     *                   APROBADO, OBSERVADO, RECHAZADO
     * @param fechaDesde (opcional) fecha mínima de registro (yyyy-MM-dd)
     * @param fechaHasta (opcional) fecha máxima de registro (yyyy-MM-dd)
     * @param tipoTramite (opcional) usado como tipoInforme: PARCIAL o FINAL
     */
    @GetMapping("/informes-avance")
    public ResponseEntity<PaginatedResponse<ReporteInformeAvance>> reporteInformesAvance(
            @RequestParam(required = false) Integer idGrupo,
            @RequestParam(required = false) String  estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) String  tipoTramite,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {

        FiltroReporte filtro = new FiltroReporte();
        filtro.setIdGrupo(idGrupo);
        filtro.setEstado(estado);
        filtro.setFechaDesde(fechaDesde);
        filtro.setFechaHasta(fechaHasta);
        filtro.setTipoTramite(tipoTramite);
        filtro.setPage(page);
        filtro.setSize(size);
        return ResponseEntity.ok(service.generarReporteInformes(filtro));
    }
}
