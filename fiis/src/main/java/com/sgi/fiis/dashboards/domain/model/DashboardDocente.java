package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDocente {

    private int proyectosComoResponsable;
    private int proyectosComoIntegrante;
    private int tramitesPendientes;
    private int informesAvancePendientes;
    private int documentosCargados;
    private int resolucionesRecibidas;

    private int proyectosPostulados;
    private int proyectosAprobados;
    private int proyectosEnEjecucion;
    private int proyectosFinalizados;

    private List<AlertaItem> alertas;
}