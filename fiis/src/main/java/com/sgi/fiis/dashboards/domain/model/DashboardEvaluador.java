package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardEvaluador {

    // Métricas del evaluador (RF-93)
    private int evaluacionesAsignadas;
    private int evaluacionesPendientes;
    private int evaluacionesCompletadas;
    private int proyectosAsignados;
    private int planesTesisAsignados;

    // Resultados de evaluaciones
    private int evaluacionesAprobadas;
    private int evaluacionesRechazadas;
    private int evaluacionesConObservaciones;

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