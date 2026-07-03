package com.sgi.fiis.resolutions.infrastructure.mapper;

import com.sgi.fiis.resolutions.domain.model.Resolution;
import com.sgi.fiis.resolutions.infrastructure.entity.ResolutionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResolutionMapper Unit Tests")
class ResolutionMapperTest {

    private final ResolutionMapper mapper = new ResolutionMapper();

    @Test
    @DisplayName("Should map entity to domain successfully")
    void toDomain_Success() {
        ResolutionEntity entity = new ResolutionEntity();
        entity.setIdResolucion(1L);
        entity.setNumeroResolucion("RES-2023-001");
        entity.setFechaEmision(LocalDate.of(2023, 10, 1));
        entity.setAsunto("Asunto de prueba");
        entity.setIdTramite(2L);
        entity.setIdDocumentoAdjunto(3L);
        entity.setFechaRegistro(LocalDateTime.of(2023, 10, 1, 10, 0));

        Resolution domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.idResolucion());
        assertEquals("RES-2023-001", domain.numeroResolucion());
        assertEquals(LocalDate.of(2023, 10, 1), domain.fechaEmision());
        assertEquals("Asunto de prueba", domain.asunto());
        assertEquals(2L, domain.idTramite());
        assertEquals(3L, domain.idDocumentoAdjunto());
        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0), domain.fechaRegistro());
    }

    @Test
    @DisplayName("Should return null when entity is null")
    void toDomain_Null() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    @DisplayName("Should map domain to entity successfully")
    void toEntity_Success() {
        Resolution domain = new Resolution(
                1L,
                "RES-2023-001",
                LocalDate.of(2023, 10, 1),
                "Asunto de prueba",
                2L,
                3L,
                LocalDateTime.of(2023, 10, 1, 10, 0)
        );

        ResolutionEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals(1L, entity.getIdResolucion());
        assertEquals("RES-2023-001", entity.getNumeroResolucion());
        assertEquals(LocalDate.of(2023, 10, 1), entity.getFechaEmision());
        assertEquals("Asunto de prueba", entity.getAsunto());
        assertEquals(2L, entity.getIdTramite());
        assertEquals(3L, entity.getIdDocumentoAdjunto());
        assertEquals(LocalDateTime.of(2023, 10, 1, 10, 0), entity.getFechaRegistro());
    }

    @Test
    @DisplayName("Should return null when domain is null")
    void toEntity_Null() {
        assertNull(mapper.toEntity(null));
    }
}
