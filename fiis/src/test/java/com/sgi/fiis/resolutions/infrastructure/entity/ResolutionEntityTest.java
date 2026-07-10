package com.sgi.fiis.resolutions.infrastructure.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class ResolutionEntityTest {

    @Test
    void testGettersAndSetters() {
        ResolutionEntity entity = new ResolutionEntity();
        
        entity.setIdResolucion(1L);
        entity.setNumeroResolucion("RES-001");
        entity.setFechaEmision(LocalDate.of(2023, Month.OCTOBER, 1));
        entity.setAsunto("Asunto");
        entity.setIdTramite(2L);
        entity.setIdDocumentoAdjunto(3L);
        
        LocalDateTime now = LocalDateTime.now();
        entity.setFechaRegistro(now);

        assertEquals(1L, entity.getIdResolucion());
        assertEquals("RES-001", entity.getNumeroResolucion());
        assertEquals(LocalDate.of(2023, Month.OCTOBER, 1), entity.getFechaEmision());
        assertEquals("Asunto", entity.getAsunto());
        assertEquals(2L, entity.getIdTramite());
        assertEquals(3L, entity.getIdDocumentoAdjunto());
        assertEquals(now, entity.getFechaRegistro());
    }

    @Test
    void testOnCreate() {
        ResolutionEntity entity = new ResolutionEntity();
        assertNull(entity.getFechaRegistro());

        entity.onCreate();

        assertNotNull(entity.getFechaRegistro());
        
        // Ensure it doesn't overwrite if already set
        LocalDateTime fixed = LocalDateTime.of(2020, Month.JANUARY, 1, 12, 0);
        entity.setFechaRegistro(fixed);
        entity.onCreate();
        
        assertEquals(fixed, entity.getFechaRegistro());
    }
}
