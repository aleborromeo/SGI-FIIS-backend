package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDecano {

    private int totalProyectosFacultad;
    private int proyectosActivos;
    private int tramitesPendientesFirma;
    private int resolucionesEmitidas;
    private int convocatoriasActivas;
    private int totalGruposActivos;

    private int tramitesEnEspera;
    private int tramitesAprobadosMes;
    private int tramitesRechazadosMes;

    private List<AlertaItem> alertas;
}