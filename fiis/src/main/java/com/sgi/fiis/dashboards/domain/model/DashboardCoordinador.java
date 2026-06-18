package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardCoordinador {

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

    private List<AlertaItem> alertas;
}