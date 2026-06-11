package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardCoordinadorResponse {

    private int idGrupo;
    private String nombreGrupo;
    private String codigoGrupo;

    private int totalMiembros;
    private int miembrosActivos;
    private int totalProyectosGrupo;
    private int proyectosActivosGrupo;
    private int tramitesPendientesGrupo;
    private int informesAvanceGrupo;
    private int planesTesisGrupo;

    private int tramitesPostulados;
    private int tramitesEnRevision;
    private int tramitesAprobados;
    private int tramitesObservados;

    private List<AlertaItemResponse> alertas;

    @Getter
    @Builder
    public static class AlertaItemResponse {
        private String tipo;
        private String titulo;
        private String descripcion;
    }
}