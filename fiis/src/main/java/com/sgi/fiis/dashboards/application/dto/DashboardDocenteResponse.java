package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardDocenteResponse {

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

    private List<AlertaItemResponse> alertas;

    @Getter
    @Builder
    public static class AlertaItemResponse {
        private String tipo;
        private String titulo;
        private String descripcion;
    }
}