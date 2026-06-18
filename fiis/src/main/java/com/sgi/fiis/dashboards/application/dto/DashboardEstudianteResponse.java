package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardEstudianteResponse {

    private int planesTesisPresentados;
    private String estadoPlanActual;
    private int tramitesPendientes;
    private int documentosCargados;
    private int convocatoriasAbiertas;

    private String nombreGrupo;
    private String codigoGrupo;

    private List<AlertaItemResponse> alertas;
}