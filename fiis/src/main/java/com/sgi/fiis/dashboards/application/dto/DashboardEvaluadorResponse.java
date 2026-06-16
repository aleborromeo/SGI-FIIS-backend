package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardEvaluadorResponse {

    private int evaluacionesAsignadas;
    private int evaluacionesPendientes;
    private int evaluacionesCompletadas;
    private int proyectosAsignados;
    private int planesTesisAsignados;

    private int evaluacionesAprobadas;
    private int evaluacionesRechazadas;
    private int evaluacionesConObservaciones;

    private List<AlertaItemResponse> alertas;
}