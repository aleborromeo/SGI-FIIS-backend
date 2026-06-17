package com.sgi.fiis.dashboards.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlertaItem {

    private String tipo;
    private String titulo;
    private String descripcion;
}