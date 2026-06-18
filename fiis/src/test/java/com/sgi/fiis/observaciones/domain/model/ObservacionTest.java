package com.sgi.fiis.observaciones.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Observacion Domain Unit Tests")
class ObservacionTest {

    @Test
    @DisplayName("Should create a valid Observacion with PENDIENTE status")
    void shouldCreateValidObservacion() {
        Observacion obs = Observacion.crear(
                1, 10, TipoObservacion.TECNICA,
                "Falta firma", "COORDINADOR_GRUPO"
        );

        assertNotNull(obs);
        assertNull(obs.getId());
        assertEquals(1, obs.getIdTramite());
        assertEquals(10, obs.getIdRevisor());
        assertEquals(TipoObservacion.TECNICA, obs.getTipoObservacion());
        assertEquals("Falta firma", obs.getDescripcion());
        assertEquals(ObservacionEstado.PENDIENTE, obs.getEstado());
        assertEquals("COORDINADOR_GRUPO", obs.getRolRevisor());
        assertNotNull(obs.getFechaRegistro());
        assertNotNull(obs.getFechaActualizacion());
    }

    @Test
    @DisplayName("Should change status to SUBSANADA when marking as subsanada")
    void shouldMarkAsSubsanada() {
        Observacion obs = Observacion.crear(
                1, 10, TipoObservacion.TECNICA,
                "Falta firma", "COORDINADOR_GRUPO"
        );

        assertTrue(obs.esSubsanable());
        boolean result = obs.marcarSubsanada();

        assertTrue(result);
        assertEquals(ObservacionEstado.SUBSANADA, obs.getEstado());
        assertFalse(obs.esSubsanable());
    }

    @Test
    @DisplayName("Should fail to mark as subsanada if already subsanada")
    void shouldFailToMarkIfAlreadySubsanada() {
        Observacion obs = Observacion.builder()
                .estado(ObservacionEstado.SUBSANADA)
                .build();

        assertFalse(obs.esSubsanable());
        boolean result = obs.marcarSubsanada();

        assertFalse(result);
        assertEquals(ObservacionEstado.SUBSANADA, obs.getEstado());
    }
}
