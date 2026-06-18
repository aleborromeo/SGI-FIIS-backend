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
