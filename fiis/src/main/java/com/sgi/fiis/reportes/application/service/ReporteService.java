package com.sgi.fiis.reportes.application.service;

import com.sgi.fiis.reportes.domain.model.*;
import com.sgi.fiis.reportes.domain.repository.ReporteRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de reportes institucionales (RF-94, RF-95).
 * Orquesta las consultas del repositorio y devuelve
 * respuestas paginadas listas para el controlador.
 */
@Service
public class ReporteService {

    private final ReporteRepositoryPort repo;

    public ReporteService(ReporteRepositoryPort repo) {
        this.repo = repo;
    }

    /**
     * Genera el reporte de proyectos aplicando los filtros indicados.
     * Soporta combinación libre de: grupo, estado, fechas, investigador y convocatoria.
     */
    public PaginatedResponse<ReporteProyecto> generarReporteProyectos(FiltroReporte filtro) {
        List<ReporteProyecto> data  = repo.findProyectos(filtro);
        long                  total = repo.countProyectos(filtro);
        return new PaginatedResponse<>(data, total, filtro.getPage(), filtro.getSize());
    }

    /**
     * Genera el reporte de trámites aplicando los filtros indicados.
     * Soporta combinación libre de: grupo, estado, fechas, investigador y tipo de trámite.
     */
    public PaginatedResponse<ReporteTramite> generarReporteTramites(FiltroReporte filtro) {
        List<ReporteTramite> data  = repo.findTramites(filtro);
        long                 total = repo.countTramites(filtro);
        return new PaginatedResponse<>(data, total, filtro.getPage(), filtro.getSize());
    }

    /**
     * Genera el reporte de resoluciones aplicando los filtros indicados.
     * Soporta combinación libre de: fechas, investigador y tipo de trámite.
     */
    public PaginatedResponse<ReporteResolucion> generarReporteResoluciones(FiltroReporte filtro) {
        List<ReporteResolucion> data  = repo.findResoluciones(filtro);
        long                    total = repo.countResoluciones(filtro);
        return new PaginatedResponse<>(data, total, filtro.getPage(), filtro.getSize());
    }

    /**
     * Genera el reporte de informes de avance aplicando los filtros indicados.
     * Soporta combinación libre de: grupo, estado, fechas y tipo de informe.
     */
    public PaginatedResponse<ReporteInformeAvance> generarReporteInformes(FiltroReporte filtro) {
        List<ReporteInformeAvance> data  = repo.findInformesAvance(filtro);
        long                       total = repo.countInformesAvance(filtro);
        return new PaginatedResponse<>(data, total, filtro.getPage(), filtro.getSize());
    }
}
