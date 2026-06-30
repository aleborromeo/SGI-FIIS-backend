package com.sgi.fiis.lineas_investigacion.presentation.mapper;

import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineRequestDto;
import com.sgi.fiis.lineas_investigacion.application.dto.ResearchLineResponseDto;
import com.sgi.fiis.lineas_investigacion.domain.model.ResearchLine;
import org.springframework.stereotype.Component;

@Component
public class ResearchLineMapper {

    public ResearchLine toDomain(ResearchLineRequestDto dto) {
        if (dto == null) {
            return null;
        }
        return ResearchLine.builder()
                .lineName(dto.getLineName())
                .build();
    }

    public ResearchLineResponseDto toResponseDto(ResearchLine domain) {
        if (domain == null) {
            return null;
        }
        return ResearchLineResponseDto.builder()
                .id(domain.getId())
                .lineName(domain.getLineName())
                .active(domain.isActive())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt().toString() : null)
                .updatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt().toString() : null)
                .build();
    }
}
