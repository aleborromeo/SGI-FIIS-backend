package com.sgi.fiis.dashboards.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlertaItemResponse {

    private String tipo;
    private String titulo;
    private String descripcion;
}