package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDecanoResponse {

    private int totalProyectosFacultad;
    private int proyectosActivos;
    private int tramitesPendientesFirma;
    private int resolucionesEmitidas;
    private int convocatoriasActivas;
    private int totalGruposActivos;

    private int tramitesEnEspera;
    private int tramitesAprobadosMes;
    private int tramitesRechazadosMes;

    private List<AlertaItemResponse> alertas;
}