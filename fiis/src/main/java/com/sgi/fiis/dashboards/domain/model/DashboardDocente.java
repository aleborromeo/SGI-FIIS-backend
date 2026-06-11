package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDocente {

    // Métricas del docente (RF-92)
    private int proyectosComoResponsable;
    private int proyectosComoIntegrante;
    private int tramitesPendientes;
    private int informesAvancePendientes;
    private int documentosCargados;
    private int resolucionesRecibidas;

    // Estado de proyectos
    private int proyectosPostulados;
    private int proyectosAprobados;
    private int proyectosEnEjecucion;
    private int proyectosFinalizados;

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