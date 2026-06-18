package com.sgi.fiis.observaciones.infrastructure.persistence.mapper;

import com.sgi.fiis.observaciones.domain.model.Observacion;
import com.sgi.fiis.observaciones.domain.model.ObservacionEstado;
import com.sgi.fiis.observaciones.domain.model.TipoObservacion;
import com.sgi.fiis.observaciones.infrastructure.persistence.entity.ObservacionJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ObservacionMapper Unit Tests")
class ObservacionMapperTest {

    private final ObservacionMapper mapper = new ObservacionMapper();

    @Test
    @DisplayName("Should map JPA Entity to Domain Model")
    void shouldMapEntityToDomain() {
        LocalDateTime ahora = LocalDateTime.now();
        ObservacionJpaEntity entity = ObservacionJpaEntity.builder()
                .idObservacion(1)
                .idTramite(10)
                .idRevisor(20)
                .tipoObservacion("TECNICA")
                .descripcion("Falta firma")
                .estadoObservacion("PENDIENTE")
                .rolRevisor("COORDINADOR_GRUPO")
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();

        Observacion domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1, domain.getId());
        assertEquals(10, domain.getIdTramite());
        assertEquals(20, domain.getIdRevisor());
        assertEquals(TipoObservacion.TECNICA, domain.getTipoObservacion());
        assertEquals("Falta firma", domain.getDescripcion());
        assertEquals(ObservacionEstado.PENDIENTE, domain.getEstado());
        assertEquals("COORDINADOR_GRUPO", domain.getRolRevisor());
        assertEquals(ahora, domain.getFechaRegistro());
        assertEquals(ahora, domain.getFechaActualizacion());
    }

    @Test
    @DisplayName("Should map Domain Model to JPA Entity")
    void shouldMapDomainToEntity() {
        LocalDateTime ahora = LocalDateTime.now();
        Observacion domain = Observacion.builder()
                .id(1)
                .idTramite(10)
                .idRevisor(20)
                .tipoObservacion(TipoObservacion.TECNICA)
                .descripcion("Falta firma")
                .estado(ObservacionEstado.PENDIENTE)
                .rolRevisor("COORDINADOR_GRUPO")
                .fechaRegistro(ahora)
                .fechaActualizacion(ahora)
                .build();

        ObservacionJpaEntity entity = mapper.toJpa(domain);

        assertNotNull(entity);
        assertEquals(1, entity.getIdObservacion());
        assertEquals(10, entity.getIdTramite());
        assertEquals(20, entity.getIdRevisor());
        assertEquals("TECNICA", entity.getTipoObservacion());
        assertEquals("Falta firma", entity.getDescripcion());
        assertEquals("PENDIENTE", entity.getEstadoObservacion());
        assertEquals("COORDINADOR_GRUPO", entity.getRolRevisor());
        assertEquals(ahora, entity.getFechaRegistro());
        assertEquals(ahora, entity.getFechaActualizacion());
    }
}
