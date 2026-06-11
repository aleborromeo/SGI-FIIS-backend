package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDirectorResponse {

    private int totalProyectos;
    private int proyectosActivos;
    private int proyectosPostulados;
    private int proyectosObservados;
    private int tramitesPendientesRevision;
    private int informesPorVencer;
    private int resolucionesEmitidas;
    private int convocatoriasAbiertas;

    private int tramitesEnCoordinador;
    private int tramitesEnDirector;
    private int tramitesEnDecano;
    private int tramitesFinalizados;

    private List<AlertaItemResponse> alertas;

    @Getter
    @Builder
    public static class AlertaItemResponse {
        private String tipo;
        private String titulo;
        private String descripcion;
    }
}