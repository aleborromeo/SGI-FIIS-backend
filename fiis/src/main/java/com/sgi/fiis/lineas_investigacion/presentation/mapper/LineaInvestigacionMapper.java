package com.sgi.fiis.lineas_investigacion.presentation.mapper;

import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.LineaInvestigacionResponseDto;
import com.sgi.fiis.lineas_investigacion.domain.model.LineaInvestigacion;
import org.springframework.stereotype.Component;

@Component
public class LineaInvestigacionMapper {

    public LineaInvestigacion toDomain(LineaInvestigacionRequestDto dto) {
        return LineaInvestigacion.builder()
                .nombreLinea(dto.getNombreLinea())
                .build();
    }

    public LineaInvestigacionResponseDto toResponseDto(LineaInvestigacion domain) {
        return LineaInvestigacionResponseDto.builder()
                .id(domain.getId())
                .nombreLinea(domain.getNombreLinea())
                .esActiva(domain.isEsActiva())
                .fechaCreacion(domain.getFechaCreacion() != null ? domain.getFechaCreacion().toString() : null)
                .fechaActualizacion(domain.getFechaActualizacion() != null ? domain.getFechaActualizacion().toString() : null)
                .build();
    }
}
