package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDirector {

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

    private List<AlertaItem> alertas;
}