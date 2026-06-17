package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardEvaluador {

    private int evaluacionesAsignadas;
    private int evaluacionesPendientes;
    private int evaluacionesCompletadas;
    private int proyectosAsignados;
    private int planesTesisAsignados;

    private int evaluacionesAprobadas;
    private int evaluacionesRechazadas;
    private int evaluacionesConObservaciones;

    private List<AlertaItem> alertas;
}