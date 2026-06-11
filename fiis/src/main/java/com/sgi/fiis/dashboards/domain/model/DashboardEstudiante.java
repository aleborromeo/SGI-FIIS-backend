package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardEstudiante {

    // Métricas del estudiante/tesista
    private int planesTesisPresentados;
    private int estadoPlanActual;   // uso string en DTO
    private String estadoPlanActualDescripcion;
    private int tramitesPendientes;
    private int documentosCargados;
    private int convocatoriasAbiertas;

    // Info del grupo al que pertenece
    private String nombreGrupo;
    private String codigoGrupo;

    // Alertas
    private List<AlertaItem> alertas;

    @Getter
    @Builder
    public static class AlertaItem {
        private String tipo;
        private String titulo;
        private String descripcion;
    }
}