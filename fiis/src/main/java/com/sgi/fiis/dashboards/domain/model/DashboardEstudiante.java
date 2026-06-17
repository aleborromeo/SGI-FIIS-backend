package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardEstudiante {

    private int planesTesisPresentados;
    private String estadoPlanActual;
    private int tramitesPendientes;
    private int documentosCargados;
    private int convocatoriasAbiertas;

    private String nombreGrupo;
    private String codigoGrupo;

    private List<AlertaItem> alertas;
}