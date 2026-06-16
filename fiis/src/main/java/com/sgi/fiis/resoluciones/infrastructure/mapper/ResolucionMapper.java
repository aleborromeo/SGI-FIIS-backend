package com.sgi.fiis.resoluciones.infrastructure.mapper;

import com.sgi.fiis.resoluciones.domain.model.Resolucion;
import com.sgi.fiis.resoluciones.infrastructure.entity.ResolucionEntity;
import org.springframework.stereotype.Component;

@Component
public class ResolucionMapper {

    public Resolucion toDomain(ResolucionEntity entity) {
        if (entity == null) return null;
        return new Resolucion(
                entity.getIdResolucion(),
                entity.getNumeroResolucion(),
                entity.getFechaEmision(),
                entity.getAsunto(),
                entity.getIdTramite(),
                entity.getIdDocumentoAdjunto(),
                entity.getFechaRegistro()
        );
    }

    public ResolucionEntity toEntity(Resolucion domain) {
        if (domain == null) return null;
        ResolucionEntity entity = new ResolucionEntity();
        entity.setIdResolucion(domain.getIdResolucion());
        entity.setNumeroResolucion(domain.getNumeroResolucion());
        entity.setFechaEmision(domain.getFechaEmision());
        entity.setAsunto(domain.getAsunto());
        entity.setIdTramite(domain.getIdTramite());
        entity.setIdDocumentoAdjunto(domain.getIdDocumentoAdjunto());
        entity.setFechaRegistro(domain.getFechaRegistro());
        return entity;
    }
}
