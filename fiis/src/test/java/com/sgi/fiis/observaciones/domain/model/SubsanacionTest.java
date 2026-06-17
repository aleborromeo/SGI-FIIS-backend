package com.sgi.fiis.observaciones.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Subsanacion Domain Unit Tests")
class SubsanacionTest {

    @Test
    @DisplayName("Should create a valid Subsanacion")
    void shouldCreateValidSubsanacion() {
        Subsanacion sub = Subsanacion.crear(
                100, 5,
                "He subido el documento firmado correctamente.", 45
        );

        assertNotNull(sub);
        assertNull(sub.getId());
        assertEquals(100, sub.getIdObservacion());
        assertEquals(5, sub.getIdSolicitante());
        assertEquals("He subido el documento firmado correctamente.", sub.getDescripcion());
        assertEquals(45, sub.getIdDocumentoAdjunto());
        assertNotNull(sub.getFechaRegistro());
        assertNotNull(sub.getFechaActualizacion());
    }

    @Test
    @DisplayName("Should return true when subsanacion has document attached")
    void shouldCheckDocumentAttached() {
        Subsanacion subWithDoc = Subsanacion.builder()
                .idDocumentoAdjunto(45)
                .build();
        Subsanacion subWithoutDoc = Subsanacion.builder()
                .idDocumentoAdjunto(null)
                .build();

        assertTrue(subWithDoc.tieneDocumentoAdjunto());
        assertFalse(subWithoutDoc.tieneDocumentoAdjunto());
    }
}
