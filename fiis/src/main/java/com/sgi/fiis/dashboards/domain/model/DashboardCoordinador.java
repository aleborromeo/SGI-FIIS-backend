package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardCoordinador {

    // Info del grupo (RF-90, RF-91)
    private int idGrupo;
    private String nombreGrupo;
    private String codigoGrupo;

    // Métricas solo de su grupo (RF-91)
    private int totalMiembros;
    private int miembrosActivos;
    private int totalProyectosGrupo;
    private int proyectosActivosGrupo;
    private int tramitesPendientesGrupo;
    private int informesAvanceGrupo;
    private int planesTesisGrupo;

    // Flujo institucional del grupo
    private int tramitesPostulados;
    private int tramitesEnRevision;
    private int tramitesAprobados;
    private int tramitesObservados;

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