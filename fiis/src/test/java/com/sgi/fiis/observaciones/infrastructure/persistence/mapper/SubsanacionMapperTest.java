package com.sgi.fiis.observaciones.infrastructure.persistence.mapper;

import com.sgi.fiis.observaciones.domain.model.Subsanacion;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.SubsanacionJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SubsanacionMapper Unit Tests")
class SubsanacionMapperTest {

    private final SubsanacionMapper mapper = new SubsanacionMapper();

    @Test
    @DisplayName("Should map JPA Entity to Domain Model")
    void shouldMapEntityToDomain() {
        LocalDateTime ahora = LocalDateTime.now();
        SubsanacionJpaEntity entity = SubsanacionJpaEntity.builder()
                .idSubsanacion(1)
                .idObservacion(100)
                .idSolicitante(5)
                .descripcion("He subido el documento")
                .idDocumentoAdjunto(45)
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();

        Subsanacion domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1, domain.getId());
        assertEquals(100, domain.getIdObservacion());
        assertEquals(5, domain.getIdSolicitante());
        assertEquals("He subido el documento", domain.getDescripcion());
        assertEquals(45, domain.getIdDocumentoAdjunto());
        assertEquals(ahora, domain.getFechaRegistro());
        assertEquals(ahora, domain.getFechaActualizacion());
    }

    @Test
    @DisplayName("Should map Domain Model to JPA Entity")
    void shouldMapDomainToEntity() {
        LocalDateTime ahora = LocalDateTime.now();
        Subsanacion domain = Subsanacion.builder()
                .id(1)
                .idObservacion(100)
                .idSolicitante(5)
                .descripcion("He subido el documento")
                .idDocumentoAdjunto(45)
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();

        SubsanacionJpaEntity entity = mapper.toJpa(domain);

        assertNotNull(entity);
        assertEquals(1, entity.getIdSubsanacion());
        assertEquals(100, entity.getIdObservacion());
        assertEquals(5, entity.getIdSolicitante());
        assertEquals("He subido el documento", entity.getDescripcion());
        assertEquals(45, entity.getIdDocumentoAdjunto());
        assertEquals(ahora, entity.getFechaRegistro());
        assertEquals(ahora, entity.getFechaActualizacion());
    }
}
