package com.sgi.fiis.resolutions.infrastructure.mapper;

import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.infrastructure.entity.ResolutionEntity;
import org.springframework.stereotype.Component;

@Component
public class ResolutionMapper {

    public Resolution toDomain(ResolutionEntity entity) {
        if (entity == null) return null;
        return new Resolution(
                entity.getIdResolucion(),
                entity.getNumeroResolucion(),
                entity.getFechaEmision(),
                entity.getAsunto(),
                entity.getIdTramite(),
                entity.getIdDocumentoAdjunto(),
                entity.getFechaRegistro()
        );
    }

    public ResolutionEntity toEntity(Resolution domain) {
        if (domain == null) return null;
        ResolutionEntity entity = new ResolutionEntity();
        entity.setIdResolucion(domain.idResolucion());
        entity.setNumeroResolucion(domain.numeroResolucion());
        entity.setFechaEmision(domain.fechaEmision());
        entity.setAsunto(domain.asunto());
        entity.setIdTramite(domain.idTramite());
        entity.setIdDocumentoAdjunto(domain.idDocumentoAdjunto());
        entity.setFechaRegistro(domain.fechaRegistro());
        return entity;
    }
}
