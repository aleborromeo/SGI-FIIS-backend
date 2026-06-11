package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardAdminResponse {

    private int totalUsuarios;
    private int totalUsuariosActivos;
    private int totalGrupos;
    private int totalGruposActivos;
    private int totalProyectos;
    private int proyectosActivos;
    private int tramitesPendientes;
    private int resolucionesEmitidas;

    private int tramitesEnRevision;
    private int tramitesAprobados;
    private int tramitesRechazados;

    private List<AlertaItemResponse> alertas;

    @Getter
    @Builder
    public static class AlertaItemResponse {
        private String tipo;
        private String titulo;
        private String descripcion;
    }
}