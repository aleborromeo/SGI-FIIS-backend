package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDirector {

    // Tarjetas de métricas institucionales (RF-89)
    private int totalProyectos;
    private int proyectosActivos;
    private int proyectosPostulados;
    private int proyectosObservados;
    private int tramitesPendientesRevision;
    private int informesPorVencer;
    private int resolucionesEmitidas;
    private int convocatoriasAbiertas;

    // Flujo institucional
    private int tramitesEnCoordinador;
    private int tramitesEnDirector;
    private int tramitesEnDecano;
    private int tramitesFinalizados;

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