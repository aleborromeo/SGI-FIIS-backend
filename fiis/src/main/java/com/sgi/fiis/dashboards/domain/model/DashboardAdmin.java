package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardAdmin {

    // Tarjetas de métricas globales (RF-88)
    private int totalUsuarios;
    private int totalUsuariosActivos;
    private int totalGrupos;
    private int totalGruposActivos;
    private int totalProyectos;
    private int proyectosActivos;
    private int tramitesPendientes;
    private int resolucionesEmitidas;

    // Flujo institucional
    private int tramitesEnRevision;
    private int tramitesAprobados;
    private int tramitesRechazados;

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