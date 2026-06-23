package com.sgi.fiis.reportes.domain.repository;

import com.sgi.fiis.reportes.domain.model.*;

import java.util.List;

/**
 * Contrato del repositorio de reportes institucionales (RF-94, RF-95).
 * Definido en domain para aplicar Dependency Inversion:
 * application depende de esta interfaz, infrastructure la implementa.
 */
public interface ReporteRepositoryPort {

    List<ReporteProyecto>      findProyectos(FiltroReporte filtro);
    long                       countProyectos(FiltroReporte filtro);

    List<ReporteTramite>       findTramites(FiltroReporte filtro);
    long                       countTramites(FiltroReporte filtro);

    List<ReporteResolucion>    findResoluciones(FiltroReporte filtro);
    long                       countResoluciones(FiltroReporte filtro);

    List<ReporteInformeAvance> findInformesAvance(FiltroReporte filtro);
    long                       countInformesAvance(FiltroReporte filtro);
}
