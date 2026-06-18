package com.sgi.fiis.observaciones.infrastructure.persistence.mapper;

import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.SubsanacionJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper bidireccional entre {@link Subsanacion} (dominio) y {@link SubsanacionJpaEntity} (JPA).
 */
@Component
public class SubsanacionMapper {

    public Subsanacion toDomain(SubsanacionJpaEntity entity) {
        return Subsanacion.builder()
                .id(entity.getIdSubsanacion())
                .idObservacion(entity.getIdObservacion())
                .idSolicitante(entity.getIdSolicitante())
                .descripcion(entity.getDescripcion())
                .idDocumentoAdjunto(entity.getIdDocumentoAdjunto())
                .fechaRegistro(entity.getFechaRegistro())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    public SubsanacionJpaEntity toJpa(Subsanacion domain) {
        return SubsanacionJpaEntity.builder()
                .idSubsanacion(domain.getId())
                .idObservacion(domain.getIdObservacion())
                .idSolicitante(domain.getIdSolicitante())
                .descripcion(domain.getDescripcion())
                .idDocumentoAdjunto(domain.getIdDocumentoAdjunto())
                .fechaRegistro(domain.getFechaRegistro())
                .fechaActualizacion(domain.getFechaActualizacion())
                .build();
    }
}
