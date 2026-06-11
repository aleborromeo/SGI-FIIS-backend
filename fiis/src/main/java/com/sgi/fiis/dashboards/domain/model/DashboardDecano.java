package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDecano {

    // Métricas institucionales para el Decano
    private int totalProyectosFacultad;
    private int proyectosActivos;
    private int tramitesPendientesFirma;
    private int resolucionesEmitidas;
    private int convocatoriasActivas;
    private int totalGruposActivos;

    // Flujo de trámites que llegan al Decano
    private int tramitesEnEspera;
    private int tramitesAprobadosMes;
    private int tramitesRechazadosMes;

    // Alertas
    private List<AlertaItem> alertas;

    @Getter
    @Builder
    public static class AlertaItem {
        private String tipo;
        private String titulo;
        private String descripcion;
    }
}